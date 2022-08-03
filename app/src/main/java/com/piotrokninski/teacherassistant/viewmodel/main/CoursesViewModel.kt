package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.launch

class CoursesViewModel(private val dataStoreRepository: DataStoreRepository) : ViewModel() {
    private val TAG = "CoursesFragmentViewMode"

    var viewType: String? = null
    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val _courses = MutableLiveData<ArrayList<Course>>()
    val courses: LiveData<ArrayList<Course>> = _courses

    init {
        viewModelScope.launch {
            viewType = dataStoreRepository.getString(DataStoreRepository.Constants.VIEW_TYPE)
            getCourses()
        }
    }

    private suspend fun getCourses() {
        _courses.value = when (viewType) {
            AppConstants.VIEW_TYPE_STUDENT ->
                FirestoreCourseRepository.getStudiedCourses(currentUserId)

            AppConstants.VIEW_TYPE_TUTOR ->
                FirestoreCourseRepository.getTaughtCourses(currentUserId)

            else -> null
        }
    }

    class Factory(private val dataStoreRepository: DataStoreRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CoursesViewModel::class.java)) {
                return CoursesViewModel(dataStoreRepository) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}