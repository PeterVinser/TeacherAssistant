package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.viewmodel.main.NewMeetingFragmentViewModel

class NewMeetingFragmentViewModelFactory(private val invitation: Invitation?): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(NewMeetingFragmentViewModel::class.java)) {
        return NewMeetingFragmentViewModel(invitation) as T
    }
    throw IllegalArgumentException("View model not found")
}
}