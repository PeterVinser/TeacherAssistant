package com.piotrokninski.teacherassistant.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.launch

class CoursesFragmentViewModel(private val sharedPreferences: SharedPreferences): ViewModel() {

    private lateinit var viewType: String

    init {
        viewModelScope.launch {
            viewType = sharedPreferences.getString(AppConstants.VIEW_TYPE, AppConstants.VIEW_TYPE_STUDENT)!!
        }
    }
}