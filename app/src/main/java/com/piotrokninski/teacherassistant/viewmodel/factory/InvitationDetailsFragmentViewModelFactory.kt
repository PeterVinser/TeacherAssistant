package com.piotrokninski.teacherassistant.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.model.FriendInvitation
import com.piotrokninski.teacherassistant.viewmodel.InvitationDetailsFragmentViewModel

class InvitationDetailsFragmentViewModelFactory(private val invitation: FriendInvitation): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvitationDetailsFragmentViewModel::class.java)) {
            return InvitationDetailsFragmentViewModel(invitation) as T
        }
        throw IllegalArgumentException("View model not found")
    }
}