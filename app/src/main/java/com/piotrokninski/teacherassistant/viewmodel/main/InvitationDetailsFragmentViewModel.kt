package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendInvitationRepository
import com.piotrokninski.teacherassistant.util.WeekDate

class InvitationDetailsFragmentViewModel(invitation: FriendInvitation) : ViewModel(), Observable {
    private val TAG = "InvitationDetailsViewMo"

    @Bindable
    val friendInvitation = MutableLiveData<FriendInvitation>()

    @Bindable
    val course = MutableLiveData<Course?>()

    init {
        friendInvitation.value = invitation

        course.value = invitation.course
    }

    fun sendInvitation() {
        val invitation = friendInvitation.value
        invitation!!.course = course.value
        FirestoreFriendInvitationRepository.addFriendInvitation(invitation)
//        InvitationCloudFunctions.sendFriendInvitation(friendInvitation.value!!, course.value)
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

    fun addMeetingDate(meetingDate: WeekDate) {
        val meetingDates = if (course.value!!.meetingDates != null) {
            course.value!!.meetingDates!!
        } else {
            ArrayList()
        }
        meetingDates.add(meetingDate)
        course.value!!.meetingDates = meetingDates
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}