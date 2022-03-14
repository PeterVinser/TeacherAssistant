package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMeetingRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import kotlinx.coroutines.launch

class CalendarFragmentViewModel : ViewModel() {
    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val _meetings = MutableLiveData<ArrayList<Meeting>>()
    val meetings: LiveData<ArrayList<Meeting>> = _meetings

    init {
        getMeetings()
    }

    private fun getMeetings() {
        viewModelScope.launch {
            _meetings.value = FirestoreMeetingRepository.getMeetings(
                currentUserId,
                MainPreferences.getViewType()!!
            )
        }
    }
}