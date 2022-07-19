package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.launch

class CoursesViewModel : ViewModel() {
    private val TAG = "CoursesFragmentViewMode"

    var viewType: String = MainPreferences.getViewType()
        ?: throw IllegalStateException("The view type has not been initialized")

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val _courses = MutableLiveData<ArrayList<Course>>()
    val courses: LiveData<ArrayList<Course>> = _courses

    init {
        Log.d(TAG, "viewType: $viewType")
        viewModelScope.launch {
            getCourses()
        }
    }

    fun updateViewType() {
        viewType = MainPreferences.getViewType()
            ?: throw IllegalStateException("The view type has not been initialized")
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

    class Factory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CoursesViewModel::class.java)) {
                return CoursesViewModel() as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}