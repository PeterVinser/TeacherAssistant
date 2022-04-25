package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.adapteritem.CourseAdapterItem
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreCourseContract
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.launch

class CoursesFragmentViewModel : ViewModel() {
    private val TAG = "CoursesFragmentViewMode"

    var viewType: String = MainPreferences.getViewType()
        ?: throw IllegalStateException("The view type has not been initialized")

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val _courses = MutableLiveData<ArrayList<CourseAdapterItem>>()
    val courses: LiveData<ArrayList<CourseAdapterItem>> = _courses

    init {
        viewModelScope.launch {
            getCourses()
        }
    }

    fun updateViewType() {
        viewType = MainPreferences.getViewType() ?: throw IllegalStateException("The view type has not been initialized")
    }

    private suspend fun getCourses() {

        var approvedCourses: ArrayList<Course>? = null
        var pendingCourses: ArrayList<Course>? = null

        val courseItems = ArrayList<CourseAdapterItem>()

        when (viewType) {
            AppConstants.VIEW_TYPE_STUDENT -> {
                approvedCourses = FirestoreCourseRepository.getStudiedCourses(
                    currentUserId,
                    FirestoreCourseContract.STATUS_APPROVED
                )

                pendingCourses = FirestoreCourseRepository.getStudiedCourses(
                    currentUserId,
                    FirestoreCourseContract.STATUS_PENDING
                )
            }

            AppConstants.VIEW_TYPE_TUTOR -> {
                approvedCourses = FirestoreCourseRepository.getTaughtCourses(
                    currentUserId,
                    FirestoreCourseContract.STATUS_APPROVED
                )

                pendingCourses = FirestoreCourseRepository.getTaughtCourses(
                    currentUserId,
                    FirestoreCourseContract.STATUS_PENDING
                )
            }
        }

        approvedCourses?.forEach {
            courseItems.add(CourseAdapterItem.CourseItem(it))
        }

        if (!pendingCourses.isNullOrEmpty()) {
            val pendingCoursesHeader =
                CourseAdapterItem.HeaderItem(R.string.pending_courses_header_title)

            courseItems.add(pendingCoursesHeader)
        }

        pendingCourses?.forEach {
            courseItems.add(CourseAdapterItem.CourseItem(it))
        }

        _courses.value = courseItems
    }

    fun confirmCourse(course: Course) {
        course.courseId?.let { id ->
            FirestoreCourseRepository.updateCourse(
                id,
                FirestoreCourseContract.STATUS,
                FirestoreCourseContract.STATUS_APPROVED
            )
        }
    }

    fun deleteCourse(courseId: String) {
        FirestoreCourseRepository.deleteCourse(courseId)
    }
}