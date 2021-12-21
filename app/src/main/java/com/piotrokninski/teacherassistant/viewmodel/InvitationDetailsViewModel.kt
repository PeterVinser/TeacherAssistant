package com.piotrokninski.teacherassistant.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.piotrokninski.teacherassistant.model.Course
import com.piotrokninski.teacherassistant.model.Friend
import com.piotrokninski.teacherassistant.model.FriendInvitation
import com.piotrokninski.teacherassistant.model.contract.FirestoreFriendContract
import com.piotrokninski.teacherassistant.model.contract.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.repository.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.repository.FirestoreFriendInvitationRepository
import com.piotrokninski.teacherassistant.repository.FirestoreFriendRepository

class InvitationDetailsViewModel(invitation: FriendInvitation) : ViewModel(), Observable {
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
            val courseId = FirestoreCourseRepository.setCourse(course.value!!)
            if (friendInvitation.value!!.courseIds == null) {
                friendInvitation.value!!.courseIds = ArrayList()
                friendInvitation.value!!.courseIds!!.add(courseId)
            } else {
                friendInvitation.value!!.courseIds!!.add(courseId)
            }
        }
        FirestoreFriendInvitationRepository.setFriendInvitationData(friendInvitation.value!!)
        setFriends()
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