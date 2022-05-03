package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.model.meeting.MeetingInvitation
import com.piotrokninski.teacherassistant.viewmodel.main.NewMeetingFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.SearchUsersFragmentViewModel

class NewMeetingFragmentViewModelFactory(private val meetingInvitation: MeetingInvitation?, private val id: String?): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(NewMeetingFragmentViewModel::class.java)) {
        return NewMeetingFragmentViewModel(meetingInvitation, id) as T
    }
    throw IllegalArgumentException("View model not found")
}
}