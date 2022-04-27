package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.meeting.MeetingInvitation
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.firestore.*
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import kotlinx.coroutines.launch

class NewMeetingFragmentViewModel : ViewModel(), Observable {
    private val TAG = "NewMeetingFragmentViewM"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var user: User

    private val userRepository: RoomUserRepository

    private lateinit var friends: ArrayList<Friend>

    private val _friendFullNames = MutableLiveData<Array<String>>()
    val friendFullNames: LiveData<Array<String>> = _friendFullNames

    @Bindable
    val meetingInvitation = MutableLiveData<MeetingInvitation>()

    init {
        val userDao = AppDatabase.getInstance().userDao
        userRepository = RoomUserRepository(userDao)

        viewModelScope.launch {

            user = FirestoreUserRepository.getUserDataOnce(currentUserId) ?: userRepository.getUser(currentUserId)!!

            meetingInvitation.value = MeetingInvitation(invitingUserId = user.userId, invitingUserFullName = user.fullName)

            friends = FirestoreFriendRepository.getApprovedFriends(
                currentUserId,
                Friend.TYPE_ALL
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
        meetingInvitation.value!!.invitedUserId = friends[position].userId
        meetingInvitation.value!!.invitedUserFullName = friends[position].fullName
    }

    fun addMeeting(): Boolean {

        return if (meetingInvitation.value?.isComplete == true) {
            FirestoreMeetingInvitationRepository.addMeetingInvitation(meetingInvitation.value!!)
            true
        } else {
            false
        }

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}