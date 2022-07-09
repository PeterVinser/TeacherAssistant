package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreInvitationRepository
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import com.piotrokninski.teacherassistant.util.WeekDate
import kotlinx.coroutines.launch

class NewCourseFragmentViewModel(private val initInvitation: Invitation?) : ViewModel(), Observable {
    private val TAG = "NewCourseFragmentViewMo"

    private val userRepository: RoomUserRepository

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var currentUser: User

    private lateinit var students: ArrayList<Friend>

    private val _studentFullNames = MutableLiveData<Array<String>>()
    val studentFullNames: LiveData<Array<String>> = _studentFullNames

    var editing: Boolean = false

    private var selectedStudent: Friend? = null

    @Bindable
    val course = MutableLiveData<Course>()

    init {

        val userDao = AppDatabase.getInstance().userDao
        userRepository = RoomUserRepository(userDao)

        course.value = initInvitation?.course?.let { course ->
            editing = true
            course
        }  ?: Course(tutorId = currentUserId)

        viewModelScope.launch {
            currentUser = userRepository.getUser(currentUserId)!!

            course.value!!.tutorFullName = currentUser.fullName

            if (editing) {
                _studentFullNames.value = arrayListOf(initInvitation!!.course!!.studentFullName!!).toTypedArray()
            } else {
                students = FirestoreFriendRepository.getApprovedFriends(
                    currentUserId,
                    Friend.TYPE_STUDENT
                )

                if (students.isNotEmpty()) {
                    val studentFullNames = ArrayList<String>()

                    students.forEach { student ->
                        studentFullNames.add(student.fullName)
                    }

                    _studentFullNames.value = studentFullNames.toTypedArray()
                }
            }
        }
    }

    fun addCourse(): Boolean {
        return (course.value?.isComplete == true).let {

            initInvitation?.id?.let {

                initInvitation.course = course.value

                FirestoreInvitationRepository.updateInvitation(
                    initInvitation
                )
            } ?: run {

                val invitation = Invitation(
                    invitingUserId = currentUser.userId,
                    invitingUserFullName = currentUser.fullName,
                    invitedUserId = selectedStudent!!.userId,
                    invitedUserFullName = selectedStudent!!.fullName,
                    invitedAs = Invitation.Contract.STUDENT,
                    type = Invitation.Contract.TYPE_COURSE,
                    course = course.value,
                    chatId = selectedStudent!!.chatId
                )

                FirestoreInvitationRepository.addInvitation(invitation)
            }
            it
        }
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

    fun onStudentSelected(position: Int) {
        selectedStudent = students[position]

        course.value!!.studentId = selectedStudent!!.userId
        course.value!!.studentFullName = selectedStudent!!.fullName
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}