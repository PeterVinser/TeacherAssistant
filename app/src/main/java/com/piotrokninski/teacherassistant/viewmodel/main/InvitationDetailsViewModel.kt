package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.BR
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.Meeting
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreInvitationRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.util.WeekDate
import kotlinx.coroutines.launch
import java.util.*

class InvitationDetailsViewModel(newInvitationType: String?, preparedInvitation: Invitation?, invitationId: String?) :
    ViewModel(), Observable {
    private val TAG = "InvitationDetailsViewMo"

    private val registry = PropertyChangeRegistry()

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var currentUser: User

    private lateinit var friends: ArrayList<Friend>

    private val _friendFullNames = MutableLiveData<Array<String>>()
    val friendFullNames: LiveData<Array<String>> = _friendFullNames

    @Bindable
    val invitation = MutableLiveData<Invitation>()

    @Bindable
    val course = MutableLiveData<Course?>()

    @Bindable
    val meeting = MutableLiveData<Meeting?>()

    init {
        viewModelScope.launch {

            if (newInvitationType != null) {
                currentUser = FirestoreUserRepository.getUserDataOnce(currentUserId)!!

                // Getting only students in case of course invitations
                val friendType = when (newInvitationType) {
                    Invitation.Contract.TYPE_COURSE -> Friend.Contract.TYPE_STUDENT
                    else -> Friend.Contract.TYPE_ALL
                }

                friends = FirestoreFriendRepository.getApprovedFriends(
                    currentUserId,
                    friendType
                )

                if (friends.isNotEmpty()) {
                    val friendFullNames = ArrayList<String>()

                    friends.forEach {
                        friendFullNames.add(it.fullName)
                    }

                    _friendFullNames.value = friendFullNames.toTypedArray()
                }

                val invitedAs = when (newInvitationType) {
                    Invitation.Contract.TYPE_MEETING -> Invitation.Contract.FRIEND
                    else -> Invitation.Contract.STUDENT
                }

                invitation.value = Invitation(
                    invitingUserId = currentUserId,
                    invitingUserFullName = currentUser.fullName,
                    invitedAs = invitedAs,
                    type = newInvitationType
                )

                when (newInvitationType) {
                    Invitation.Contract.TYPE_COURSE -> {
                        course.value = Course(
                            tutorId = currentUserId,
                            tutorFullName = currentUser.fullName
                        )

                        invitation.value?.course = course.value
                    }

                    Invitation.Contract.TYPE_MEETING -> {
                        meeting.value = Meeting(
                            singular = true,
                            completed = false
                        )

                        invitation.value?.meeting = meeting.value
                    }
                }
            } else {

                if (preparedInvitation != null) {
                    invitation.value = preparedInvitation
                } else if (invitationId != null) {
                    invitation.value = FirestoreInvitationRepository.getInvitation(invitationId)
                }

                course.value = invitation.value?.course
                meeting.value = invitation.value?.meeting

                _friendFullNames.value = if (invitation.value?.invitedUserFullName != null) {
                    arrayListOf(invitation.value?.invitedUserFullName!!).toTypedArray()
                } else {
                    null
                }
            }
        }
    }

    fun sendInvitation(): Boolean {
        val invitation = invitation.value

        return if (invitation?.isComplete == true) {
            invitation.message?.ifEmpty { invitation.message = null }
            FirestoreInvitationRepository.addInvitation(invitation)

            true
        } else {
            false
        }
    }

    fun updateInvitation(): Boolean {
        return invitation.value?.let {
            FirestoreInvitationRepository.updateInvitation(it)
            true
        } ?: false
    }

    fun confirmInvitation(): Boolean {
        return invitation.value?.id?.let {
            FirestoreInvitationRepository.updateInvitation(
                it, Invitation.Contract.STATUS, Invitation.Contract.STATUS_APPROVED
            )
            true
        } ?: false
    }

    fun addCourse() {
        if (course.value == null) {
            course.value = Course.createCourseWithInvitation(invitation.value!!)
            invitation.value?.course = course.value
        }
    }

    fun removeCourse() {
        course.value = null
        invitation.value?.course = null
    }

    fun setCourseType(type: String) {
        course.value!!.type = type
    }

    fun addMeetingDate(meetingDate: WeekDate) {
        val meetingDates = if (course.value!!.weekDates != null) {
            course.value!!.weekDates!!
        } else {
            ArrayList()
        }
        meetingDates.add(meetingDate)
        course.value!!.weekDates = meetingDates
    }

    fun setSelectedFriend(position: Int) {

        val selectedFriend = friends[position]

        invitation.value?.invitedUserId = selectedFriend.userId
        invitation.value?.invitedUserFullName = selectedFriend.fullName
        invitation.value?.chatId = selectedFriend.chatId

        when (invitation.value?.type) {
            Invitation.Contract.TYPE_COURSE -> {
                course.value?.studentId = selectedFriend.userId
                course.value?.studentFullName = selectedFriend.fullName
            }

            Invitation.Contract.TYPE_MEETING -> {
                meeting.value?.attendeeIds = arrayListOf(currentUserId, selectedFriend.userId)
            }
        }
    }

    fun updateMeetingType(singular: Boolean) {
        meeting.value?.singular = singular

        registry.notifyChange(this, BR.meeting)
    }

    fun onSingularDateSelected(date: Date, durationHours: Int, durationMinutes: Int) {
        meeting.value!!.date = date
        meeting.value!!.durationHours = durationHours
        meeting.value!!.durationMinutes = durationMinutes

        meeting.value?.weekDate = null

        registry.notifyChange(this, BR.meeting)
    }

    fun onRecurringDateSelected(weekDate: WeekDate) {
        meeting.value!!.weekDate = weekDate

        meeting.value!!.date = weekDate.nextDate
        meeting.value!!.durationHours = weekDate.durationHours
        meeting.value!!.durationMinutes = weekDate.durationMinutes

        registry.notifyChange(this, BR.meeting)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        registry.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        registry.remove(callback)
    }

    class Factory(private val type: String?, private val invitation: Invitation?, private val invitationId: String?) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InvitationDetailsViewModel::class.java)) {
                return InvitationDetailsViewModel(type, invitation, invitationId) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}