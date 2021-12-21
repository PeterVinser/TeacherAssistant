package com.piotrokninski.teacherassistant.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.model.FriendInvitation
import com.piotrokninski.teacherassistant.viewmodel.InvitationDetailsViewModel

class InvitationDetailsViewModelFactory(private val invitation: FriendInvitation): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvitationDetailsViewModel::class.java)) {
            return InvitationDetailsViewModel(invitation) as T
        }
        throw IllegalArgumentException("View model not found")
    }
}