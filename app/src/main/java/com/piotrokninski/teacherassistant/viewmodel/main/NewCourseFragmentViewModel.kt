package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.cloudfunctions.CourseCloudFunctions
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import kotlinx.coroutines.launch

class NewCourseFragmentViewModel(private val initCourse: Course?): ViewModel(), Observable {
    private val TAG = "NewCourseFragmentViewMo"

    private val userRepository: RoomUserRepository

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var currentUser: User

    private lateinit var students: ArrayList<Friend>

    private val _studentFullNames = MutableLiveData<Array<String>>()
    val studentFullNames: LiveData<Array<String>> = _studentFullNames

    var editing: Boolean = false

    @Bindable
    val course = MutableLiveData<Course>()

    init {

        val userDao = AppDatabase.getInstance().userDao
        userRepository = RoomUserRepository(userDao)

        if (initCourse != null) {
            course.value = initCourse

            editing = true

            viewModelScope.launch {
                students = arrayListOf(FirestoreFriendRepository.getFriendDataOnce(currentUserId, initCourse.studentId!!)!!)

                _studentFullNames.value = arrayListOf(initCourse.studentFullName!!).toTypedArray()
            }
        } else {
            course.value = Course(tutorId = currentUserId)

            viewModelScope.launch {

                currentUser = userRepository.getUser(currentUserId)!!

                course.value!!.tutorFullName = currentUser.fullName

                students = FirestoreFriendRepository.getApprovedFriends(currentUserId, FirestoreFriendContract.TYPE_STUDENT)

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

    fun addCourse() {
        CourseCloudFunctions.addCourse(course.value!!)
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

    fun onStudentSelected(position: Int) {
        val selectedStudent = students[position]

        course.value!!.studentId = selectedStudent.userId
        course.value!!.studentFullName = selectedStudent.fullName
    }

    fun checkCourse(): Boolean {
         return (!(course.value!!.studentId == null || course.value!!.meetingsDates == null || course.value!!.subject == null || course.value!!.type == null))
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}