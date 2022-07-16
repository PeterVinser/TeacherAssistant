package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.*
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.course.Lesson
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreLessonRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class CourseDetailsViewModel(course: Course): ViewModel() {
    private val TAG = "CourseDetailsFragmentVi"

    private val _lessons = MutableLiveData<List<Lesson>>()
    val lessons: LiveData<List<Lesson>> = _lessons

    var viewType: String = MainPreferences.getViewType()
        ?: throw IllegalStateException("The view type has not been initialized")

    private val _course = MutableLiveData<Course>()
    val course: LiveData<Course> = _course

    init {

        viewModelScope.launch {

            _lessons.value = FirestoreLessonRepository.getCourseLessons(course.courseId!!)
        }

        _course.value = course
    }

    fun addLesson(lesson: Lesson) {
        viewModelScope.launch {
            FirestoreLessonRepository.addLesson(lesson)
        }

        val auxList = ArrayList<Lesson>()

        if (lessons.value != null) {
            auxList.addAll(lessons.value!!)
        }

        auxList.add(lesson)
        _lessons.value = auxList
    }

    class Factory(private val course: Course): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CourseDetailsViewModel::class.java)) {
                return CourseDetailsViewModel(course) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}