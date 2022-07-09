package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.adapteritem.HomeAdapterItem
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreHomeworkRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreInvitationRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeFragmentViewModel : ViewModel() {
    private val TAG = "HomeFragmentViewModel"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    val viewType = MainPreferences.getViewType()
        ?: throw IllegalStateException("No specified viewType")

    private val _homeFeedItems = MutableLiveData<List<HomeAdapterItem>>()
    val homeFeedItems: LiveData<List<HomeAdapterItem>> = _homeFeedItems

    private val eventChannel = Channel<HomeEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    init {
        getItems()
    }

    private fun getItems() {
        viewModelScope.launch {

            val auxList = ArrayList<HomeAdapterItem>()

            val receivedInvitations = FirestoreInvitationRepository
                .getReceivedInvitations(currentUserId, Invitation.Contract.STATUS_PENDING)

            val sentInvitations = FirestoreInvitationRepository
                .getSentInvitations(currentUserId, Invitation.Contract.STATUS_PENDING)

            val assignedHomework = FirestoreHomeworkRepository.getHomework(
                currentUserId,
                viewType,
                Homework.STATUS_ASSIGNED
            )

            if (!receivedInvitations.isNullOrEmpty()) {
                val titleId = R.string.header_item_received_invitations

                auxList.add(HomeAdapterItem.HeaderItem(titleId))

                receivedInvitations.forEach { invitation ->
                    auxList.add(HomeAdapterItem.InvitationItem(invitation, true))
                }
            }

            if (!sentInvitations.isNullOrEmpty()) {
                val titleId = R.string.header_item_sent_invitations

                auxList.add(HomeAdapterItem.HeaderItem(titleId))

                sentInvitations.forEach { invitation ->
                    auxList.add(HomeAdapterItem.InvitationItem(invitation, false))
                }
            }

            if (!assignedHomework.isNullOrEmpty()) {
                val titleId = when (viewType) {
                    AppConstants.VIEW_TYPE_STUDENT -> R.string.homework_student_header_text

                    AppConstants.VIEW_TYPE_TUTOR -> R.string.homework_tutor_header_text

                    else -> throw IllegalArgumentException("Unknown viewType")
                }

                auxList.add(HomeAdapterItem.HeaderItem(titleId))

                assignedHomework.forEach { homework ->
                    auxList.add(HomeAdapterItem.HomeworkItem(homework))
                }
            }

            _homeFeedItems.value = auxList
        }
    }

    fun itemPositiveAction(homeAdapterItem: HomeAdapterItem) {
        when (homeAdapterItem) {
            is HomeAdapterItem.InvitationItem -> {
                if (homeAdapterItem.received) {
                    homeAdapterItem.invitation.id?.let { id ->
                        FirestoreInvitationRepository.updateInvitation(
                            id,
                            Invitation.Contract.STATUS,
                            Invitation.Contract.STATUS_APPROVED
                        )
                    }
                } else {
                    viewModelScope.launch {
                        eventChannel.send(HomeEvent.EditItemEvent(homeAdapterItem.id, homeAdapterItem))
                    }
                }
            }

            is HomeAdapterItem.HomeworkItem -> {}

            else -> {}
        }
    }

    fun itemNegativeAction(homeAdapterItem: HomeAdapterItem) {
        when (homeAdapterItem) {
            is HomeAdapterItem.InvitationItem -> {
                if (homeAdapterItem.received) {
                    homeAdapterItem.invitation.id?.let { id ->
                        FirestoreInvitationRepository.updateInvitation(
                            id,
                            Invitation.Contract.STATUS,
                            Invitation.Contract.STATUS_REJECTED
                        )
                    }
                } else {
                    homeAdapterItem.invitation.id?.let { id ->
                        FirestoreInvitationRepository.deleteInvitation(id)
                    }
                }
            }

            is HomeAdapterItem.HomeworkItem -> {}

            else -> {}
        }
    }

    sealed class HomeEvent {
        data class EditItemEvent(val itemId: String, val homeAdapterItem: HomeAdapterItem) :
            HomeEvent()
    }
}