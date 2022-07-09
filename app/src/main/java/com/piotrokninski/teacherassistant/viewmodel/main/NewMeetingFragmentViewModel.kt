package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.Meeting
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreInvitationRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import com.piotrokninski.teacherassistant.util.WeekDate
import kotlinx.coroutines.launch
import java.util.*

class NewMeetingFragmentViewModel(
    private val initInvitation: Invitation?
) : ViewModel(), Observable {
    private val TAG = "NewMeetingFragmentViewM"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var currentUser: User

    private val userRepository: RoomUserRepository

    private lateinit var friends: ArrayList<Friend>

    private var selectedFriend: Friend? = null

    private val _friendFullNames = MutableLiveData<Array<String>>()
    val friendFullNames: LiveData<Array<String>> = _friendFullNames

    @Bindable
    val meeting = MutableLiveData<Meeting>()

    var editing: Boolean = false

    init {
        val userDao = AppDatabase.getInstance().userDao
        userRepository = RoomUserRepository(userDao)

        meeting.value = initInvitation?.let { invitation ->
            editing = true
            invitation.meeting
        } ?: Meeting(
            singular = true,
            completed = false
        )

        viewModelScope.launch {
            currentUser = FirestoreUserRepository.getUserDataOnce(currentUserId) ?: userRepository.getUser(
                currentUserId
            )!!

            if (editing) {
                _friendFullNames.value =
                    arrayListOf(initInvitation!!.invitedUserFullName!!).toTypedArray()
            } else {
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
    }

    fun onFriendSelected(position: Int) {
        selectedFriend = friends[position]
    }

    fun addMeeting(): Boolean {

        if (!meeting.value!!.singular) {
            meeting.value?.weekDates?.get(0)?.let {
                meeting.value!!.date = getNextDate(it)
                meeting.value!!.durationHours = it.durationHours
                meeting.value!!.durationMinutes = it.durationMinutes
            }   
        } else {
            meeting.value!!.weekDates = null
        }

        return (meeting.value?.isComplete == true && selectedFriend != null).let {

            initInvitation?.id?.let {
                // TODO: check whether the invitation has been changed

                initInvitation.meeting = meeting.value

                FirestoreInvitationRepository.updateInvitation(
                    initInvitation
                )
            } ?: run {
                meeting.value!!.attendeeIds = listOf(currentUserId, selectedFriend!!.userId)

                val invitation = Invitation(
                    invitingUserId = currentUserId,
                    invitingUserFullName = currentUser.fullName,
                    invitedUserId = selectedFriend!!.userId,
                    invitedUserFullName = selectedFriend!!.fullName,
                    invitedAs = selectedFriend!!.friendshipType,
                    type = Invitation.Contract.TYPE_MEETING,
                    meeting = meeting.value,
                    chatId = selectedFriend!!.chatId
                )

                FirestoreInvitationRepository.addInvitation(invitation)
            }
                it
        }
    }

    private fun getNextDate(weekDate: WeekDate): Date {
        val calendar = Calendar.getInstance()

        val now = calendar.time

        val weekDay = if (weekDate.weekDay.id == 7) {
            1
        } else {
            weekDate.weekDay.id + 1
        }

        calendar.set(Calendar.DAY_OF_WEEK, weekDay)
        calendar.set(Calendar.HOUR_OF_DAY, weekDate.hour)
        calendar.set(Calendar.MINUTE, weekDate.minute)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.time < now) calendar.add(Calendar.DAY_OF_WEEK, 7)

        return calendar.time
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}