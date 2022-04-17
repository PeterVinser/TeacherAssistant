package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.meeting.MeetingInvitation
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMeetingRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreRecurringMeetingsRepository
import kotlinx.coroutines.launch

class NewMeetingFragmentViewModel : ViewModel(), Observable {
    private val TAG = "NewMeetingFragmentViewM"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var friends: ArrayList<Friend>

    private val _friendFullNames = MutableLiveData<Array<String>>()
    val friendFullNames: LiveData<Array<String>> = _friendFullNames

    private var selectedFriend: Friend? = null

    @Bindable
    val meetingInvitation = MutableLiveData<MeetingInvitation>()

    init {

        meetingInvitation.value = MeetingInvitation(attendeeIds = arrayListOf(currentUserId))

        viewModelScope.launch {
            friends = FirestoreFriendRepository.getApprovedFriends(
                currentUserId,
                FirestoreFriendContract.TYPE_ALL
            )

            if (friends.isNotEmpty()) {
                val friendFullNames = ArrayList<String>()

                friends.forEach {
                    friendFullNames.add(it.fullName)
                }

                _friendFullNames.value = friendFullNames.toTypedArray()
            }
        }
    }

    fun onFriendSelected(position: Int) {
        selectedFriend = friends[position]
    }

    fun addMeeting(): Boolean {
        if (selectedFriend == null) {
            return false
        }

        Log.d(TAG, "addMeeting: ${meetingInvitation.value}")

        when (meetingInvitation.value!!.mode) {
            MeetingInvitation.MEETING_TYPE_SINGULAR -> {
                val meeting = meetingInvitation.value?.toSingularMeeting() ?: return false

                meetingInvitation.value!!.attendeeIds.add(selectedFriend!!.userId)

                FirestoreMeetingRepository.addMeeting(meeting)
            }

            MeetingInvitation.MEETING_TYPE_RECURRING -> {
                val recurringMeeting = meetingInvitation.value?.toRecurringMeeting() ?: return false

                meetingInvitation.value!!.attendeeIds.add(selectedFriend!!.userId)

                FirestoreRecurringMeetingsRepository.addRecurringMeeting(recurringMeeting)
            }
        }

        return true
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}