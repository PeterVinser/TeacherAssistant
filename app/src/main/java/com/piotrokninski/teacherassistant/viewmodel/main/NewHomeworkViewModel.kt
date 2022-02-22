package com.piotrokninski.teacherassistant.viewmodel.main

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
import com.piotrokninski.teacherassistant.model.course.LessonSnapshot
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreHomeworkRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreLessonRepository
import com.piotrokninski.teacherassistant.util.Util
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class NewHomeworkViewModel : ViewModel(), Observable {
    private val TAG = "NewHomeworkViewModel"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var courses: ArrayList<Course>

    private val _courseSnapshots = MutableLiveData<ArrayList<String>>()
    val courseSnapshots: LiveData<ArrayList<String>> = _courseSnapshots

    private var lessonSnapshots: ArrayList<LessonSnapshot>? = null

    private val _lessonTopics = MutableLiveData<ArrayList<String>?>()
    val lessonTopics: LiveData<ArrayList<String>?> = _lessonTopics

    private val _selectedLesson = MutableLiveData<LessonSnapshot?>()
    val selectedLesson: LiveData<LessonSnapshot?> = _selectedLesson

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

        viewModelScope.launch {
            getLessons(courses[position].courseId!!)
        }

        if (homework.value == null) {

            homework.value = Homework.initHomework(selectedCourse)
        } else {
            homework.value!!.courseId = selectedCourse.courseId!!
            homework.value!!.subject = selectedCourse.subject!!
            homework.value!!.studentId = selectedCourse.studentId!!
            homework.value!!.studentFullName = selectedCourse.studentFullName!!
        }
    }

    private suspend fun getLessons(courseId: String) {
        lessonSnapshots = FirestoreLessonRepository.getCourseLessonSnapshots(courseId)

        val topics = ArrayList<String>()

        lessonSnapshots?.forEach { lessonSnapshot ->
            val topic = lessonSnapshot.topic

            topics.add(topic)
        }

        _lessonTopics.value = topics
    }

    fun onLessonSelected(position: Int) {
        val lesson = lessonSnapshots?.get(position)

        if (lesson != null) {
            _selectedLesson.value = lesson

            homework.value!!.lessonId = lesson.lessonId
            homework.value!!.topic = lesson.topic
        }
    }

    fun dueDateSelected(date: Date) {
        homework.value!!.dueDate = Util.getLocalEndOfDay(date)

        homework.value = homework.value?.copy(reminderDate = Date(Util.getLocalDateWithTime(date, 19, 0, 0).time))
    }

    fun checkHomework(): Boolean {
        return !(homework.value == null ||
                homework.value!!.dueDate == null ||
                homework.value!!.description == null ||
                homework.value!!.topic == null)
    }

    fun deleteSelectedLesson() {
        _selectedLesson.value = null

        homework.value!!.lessonId = null
    }

    fun addHomework() {

        val creationDate = Calendar.getInstance().time

        homework.value!!.creationDate = creationDate

        FirestoreHomeworkRepository.addHomework(homework.value!!)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}