package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.piotrokninski.teacherassistant.cloudfunctions.FirebaseCloudFunctions
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository

class InvitationDetailsFragmentViewModel(invitation: FriendInvitation) : ViewModel(), Observable {
    private val TAG = "InvitationDetailsViewMo"

    @Bindable
    val friendInvitation = MutableLiveData<FriendInvitation>()

    @Bindable
    val course = MutableLiveData<Course>()

    private var _courses = MutableLiveData<ArrayList<Course>>()
    val courses: LiveData<ArrayList<Course>> = _courses

    init {
        friendInvitation.value = invitation

        course.value = null
    }

    fun sendInvitation() {
        if (course.value != null) {
            if (friendInvitation.value!!.courseIds == null) {
                friendInvitation.value!!.courseIds = ArrayList()
            }
        }

        FirebaseCloudFunctions.sendFriendInvitation(friendInvitation.value!!, course.value)
    }

    private fun setFriends() {

        val invitingFriendType = when (friendInvitation.value!!.invitationType) {
            FirestoreFriendInvitationContract.TYPE_STUDENT -> FirestoreFriendContract.TYPE_TUTOR

            FirestoreFriendInvitationContract.TYPE_TUTOR -> FirestoreFriendContract.TYPE_STUDENT

            else -> throw IllegalArgumentException()
        }

        val invitingFriend = Friend(friendInvitation.value!!.invitingUserId, friendInvitation.value!!.invitingUserFullName,
            FirestoreFriendContract.STATUS_INVITING, invitingFriendType)

        val invitedFriend = Friend(friendInvitation.value!!.invitedUserId, friendInvitation.value!!.invitedUserFullName,
            FirestoreFriendContract.STATUS_INVITED, friendInvitation.value!!.invitationType)

        FirestoreFriendRepository.setFriendData(invitedFriend.userId, invitingFriend)
        FirestoreFriendRepository.setFriendData(invitingFriend.userId, invitedFriend)
    }

    fun addCourse(type: String) {
        if (course.value == null) {
            course.value = Course.createCourseWithInvitation(friendInvitation.value!!)
            course.value!!.type = type
        }
    }

    fun removeCourse() {
        course.value = null
    }

    fun setCourseType(type: String) {
        course.value!!.type = type
    }

    fun addMeetingDate(meetingDate: String) {
        if (course.value!!.meetingsDates != null) {
            val meetingsTime = course.value!!.meetingsDates!!
            meetingsTime.add(meetingDate)
            course.value!!.meetingsDates = meetingsTime
        } else {
            course.value!!.meetingsDates = arrayListOf(meetingDate)
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}