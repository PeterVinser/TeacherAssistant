package com.piotrokninski.teacherassistant.viewmodel.factory

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.CoursesFragmentViewModel

class CoursesFragmentViewModelFactory(private val sharedPreferences: SharedPreferences): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoursesFragmentViewModel::class.java)) {
            return CoursesFragmentViewModel(sharedPreferences) as T
        }
        throw IllegalArgumentException("View model not found")
    }
}