package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreHomeworkRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreInvitationRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import com.piotrokninski.teacherassistant.view.main.adapter.InvitationsAdapter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class InvitationsViewModel : ViewModel() {
    private val TAG = "HomeFragmentViewModel"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    val viewType = MainPreferences.getViewType()
        ?: throw IllegalStateException("No specified viewType")

    private val _homeFeedItems = MutableLiveData<List<InvitationsAdapter.Item>>()
    val homeFeedItems: LiveData<List<InvitationsAdapter.Item>> = _homeFeedItems

    private val eventChannel = Channel<HomeEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    init {
        getItems()
    }

    private fun getItems() {
        viewModelScope.launch {

            val auxList = ArrayList<InvitationsAdapter.Item>()

            val receivedInvitations = FirestoreInvitationRepository
                .getReceivedInvitations(currentUserId, Invitation.Contract.STATUS_PENDING)

            val sentInvitations = FirestoreInvitationRepository
                .getSentInvitations(currentUserId, Invitation.Contract.STATUS_PENDING)

            val assignedHomework = FirestoreHomeworkRepository.getHomework(
                currentUserId,
                viewType,
                Homework.Contract.STATUS_ASSIGNED
            )

            if (!receivedInvitations.isNullOrEmpty()) {
                val titleId = R.string.header_item_received_invitations

                auxList.add(InvitationsAdapter.Item.Header(titleId))

                receivedInvitations.forEach { invitation ->
                    auxList.add(InvitationsAdapter.Item.Invitation(invitation, true))
                }
            }

            if (!sentInvitations.isNullOrEmpty()) {
                val titleId = R.string.header_item_sent_invitations

                auxList.add(InvitationsAdapter.Item.Header(titleId))

                sentInvitations.forEach { invitation ->
                    auxList.add(InvitationsAdapter.Item.Invitation(invitation, false))
                }
            }

            if (!assignedHomework.isNullOrEmpty()) {
                val titleId = when (viewType) {
                    AppConstants.VIEW_TYPE_STUDENT -> R.string.homework_student_header_text

                    AppConstants.VIEW_TYPE_TUTOR -> R.string.homework_tutor_header_text

                    else -> throw IllegalArgumentException("Unknown viewType")
                }

                auxList.add(InvitationsAdapter.Item.Header(titleId))

                assignedHomework.forEach { homework ->
                    auxList.add(InvitationsAdapter.Item.Homework(homework))
                }
            }

            _homeFeedItems.value = auxList
        }
    }

    fun itemPositiveAction(invitationsAdapterItem: InvitationsAdapter.Item) {
        when (invitationsAdapterItem) {
            is InvitationsAdapter.Item.Invitation -> {
                if (invitationsAdapterItem.received) {
                    invitationsAdapterItem.invitation.id?.let { id ->
                        FirestoreInvitationRepository.updateInvitation(
                            id,
                            Invitation.Contract.STATUS,
                            Invitation.Contract.STATUS_APPROVED
                        )
                    }
                } else {
                    viewModelScope.launch {
                        eventChannel.send(
                            HomeEvent.EditItemEvent(invitationsAdapterItem.id, invitationsAdapterItem)
                        )
                    }
                }
            }

            is InvitationsAdapter.Item.Homework -> {}

            else -> {}
        }
    }

    fun itemNegativeAction(invitationsAdapterItem: InvitationsAdapter.Item) {
        when (invitationsAdapterItem) {
            is InvitationsAdapter.Item.Invitation -> {
                if (invitationsAdapterItem.received) {
                    invitationsAdapterItem.invitation.id?.let { id ->
                        FirestoreInvitationRepository.updateInvitation(
                            id,
                            Invitation.Contract.STATUS,
                            Invitation.Contract.STATUS_REJECTED
                        )
                    }
                } else {
                    invitationsAdapterItem.invitation.id?.let { id ->
                        FirestoreInvitationRepository.deleteInvitation(id)
                    }
                }
            }

            is InvitationsAdapter.Item.Homework -> {}

            else -> {}
        }
    }

    sealed class HomeEvent {
        data class EditItemEvent(val itemId: String, val invitationsAdapterItem: InvitationsAdapter.Item) :
            HomeEvent()
    }

    class Factory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InvitationsViewModel::class.java)) {
                return InvitationsViewModel() as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}