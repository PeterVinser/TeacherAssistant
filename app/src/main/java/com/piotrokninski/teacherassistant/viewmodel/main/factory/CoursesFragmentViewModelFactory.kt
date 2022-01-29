package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.main.CoursesFragmentViewModel

class CoursesFragmentViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoursesFragmentViewModel::class.java)) {
            return CoursesFragmentViewModel() as T
        }
        throw IllegalArgumentException("View model not found")
    }
}