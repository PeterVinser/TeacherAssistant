package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.viewmodel.main.CourseDetailsFragmentViewModel

class CourseDetailsFragmentViewModelFactory(private val course: Course) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(CourseDetailsFragmentViewModel::class.java)) {
        return CourseDetailsFragmentViewModel(course) as T
    }
    throw IllegalArgumentException("View model not found")
}
}