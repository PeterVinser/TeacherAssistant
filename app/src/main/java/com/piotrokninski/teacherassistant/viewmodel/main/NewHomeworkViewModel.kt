package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreCourseContract
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreHomeworkRepository
import kotlinx.coroutines.launch
import java.util.*

class NewHomeworkViewModel : ViewModel(), Observable {
    private val TAG = "NewHomeworkViewModel"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var courses: ArrayList<Course>

    private val _courseSnapshots = MutableLiveData<ArrayList<String>>()
    val courseSnapshots: LiveData<ArrayList<String>> = _courseSnapshots

    @Bindable
    val homework = MutableLiveData<Homework>()

    init {
        viewModelScope.launch {
            courses = FirestoreCourseRepository.getTaughtCourses(
                currentUserId,
                FirestoreCourseContract.STATUS_APPROVED
            )!!

            createCourseSnapshots()
        }
    }

    private fun createCourseSnapshots() {
        val snapshots = ArrayList<String>()

        courses.forEach { course ->
            val subject = course.subject
            val studentFullName = course.studentFullName

            snapshots.add("$subject - $studentFullName")
        }

        _courseSnapshots.value = snapshots
    }

    fun onCourseSelected(position: Int) {
        val selectedCourse = courses[position]

        if (homework.value == null) {

            homework.value = Homework.initHomework(selectedCourse)
        } else {
            homework.value!!.courseId = selectedCourse.courseId!!
            homework.value!!.subject = selectedCourse.subject!!
            homework.value!!.studentId = selectedCourse.studentId!!
            homework.value!!.studentFullName = selectedCourse.studentFullName!!
        }
    }

    fun dueDateSelected(date: Date) {
        homework.value!!.dueDate = date
    }

    fun checkHomework(): Boolean {
        return !(homework.value == null || homework.value!!.dueDate == null || homework.value!!.description == null)
    }

    fun addHomework() {
        FirestoreHomeworkRepository.addHomework(homework.value!!)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}