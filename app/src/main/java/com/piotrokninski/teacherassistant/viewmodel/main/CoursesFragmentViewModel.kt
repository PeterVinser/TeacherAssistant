package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class CoursesFragmentViewModel : ViewModel() {
    private val TAG = "CoursesFragmentViewMode"

    lateinit var viewType: String

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val _courses = MutableLiveData<ArrayList<Course>>()
    val courses: LiveData<ArrayList<Course>> = _courses

    init {
        viewType = MainPreferences.getViewType() ?: throw IllegalStateException("The view type has not been initialized")

        viewModelScope.launch {

            when (viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> _courses.value = FirestoreCourseRepository.getStudiedCourses(currentUserId)

                AppConstants.VIEW_TYPE_TUTOR -> _courses.value = FirestoreCourseRepository.getTaughtCourses(currentUserId)
            }
        }
    }
}