package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.*
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.course.Lesson
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreLessonRepository
import kotlinx.coroutines.launch

class CourseDetailsViewModel(private val dataStoreRepository: DataStoreRepository, course: Course): ViewModel() {
    private val TAG = "CourseDetailsFragmentVi"

    private val _lessons = MutableLiveData<List<Lesson>>()
    val lessons: LiveData<List<Lesson>> = _lessons

    var viewType: String? = null

    private val _course = MutableLiveData<Course>()
    val course: LiveData<Course> = _course

    init {

        viewModelScope.launch {
            viewType = dataStoreRepository.getString(DataStoreRepository.Constants.VIEW_TYPE)

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

    class Factory(
        private val dataStoreRepository: DataStoreRepository, private val course: Course
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CourseDetailsViewModel::class.java)) {
                return CourseDetailsViewModel(dataStoreRepository, course) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}