package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.viewmodel.main.InvitationFragmentViewModel

class InvitationFragmentViewModelFactory(private val type: String, private val invitation: Invitation?): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvitationFragmentViewModel::class.java)) {
            return InvitationFragmentViewModel(type, invitation) as T
        }
        throw IllegalArgumentException("View model not found")
    }
}