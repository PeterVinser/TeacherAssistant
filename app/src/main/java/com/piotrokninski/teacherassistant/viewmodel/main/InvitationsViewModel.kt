package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreInvitationRepository
import com.piotrokninski.teacherassistant.view.main.adapter.InvitationsAdapter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class InvitationsViewModel(private val dataStoreRepository: DataStoreRepository) : ViewModel() {
    private val TAG = "HomeFragmentViewModel"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    var viewType: String? = null

    private val _homeFeedItems = MutableLiveData<List<InvitationsAdapter.Item>>()
    val homeFeedItems: LiveData<List<InvitationsAdapter.Item>> = _homeFeedItems

    private val eventChannel = Channel<HomeEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            viewType = dataStoreRepository.getString(DataStoreRepository.Constants.VIEW_TYPE)
            getItems()
        }
    }

    private suspend fun getItems() {

        val auxList = ArrayList<InvitationsAdapter.Item>()

        val receivedInvitations = FirestoreInvitationRepository
            .getReceivedInvitations(currentUserId, Invitation.Contract.STATUS_PENDING)

        val sentInvitations = FirestoreInvitationRepository
            .getSentInvitations(currentUserId, Invitation.Contract.STATUS_PENDING)

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

        _homeFeedItems.value = auxList    }

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

    class Factory(private val dataStoreRepository: DataStoreRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InvitationsViewModel::class.java)) {
                return InvitationsViewModel(dataStoreRepository) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}