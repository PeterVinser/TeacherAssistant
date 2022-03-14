package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.main.CalendarFragmentViewModel

class CalendarFragmentViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarFragmentViewModel::class.java)) {
            return CalendarFragmentViewModel() as T
        }
        throw IllegalArgumentException("View model not found")
    }
}