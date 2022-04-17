package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.main.NewMeetingFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.SearchUsersFragmentViewModel

class NewMeetingFragmentViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(NewMeetingFragmentViewModel::class.java)) {
        return NewMeetingFragmentViewModel() as T
    }
    throw IllegalArgumentException("View model not found")
}
}