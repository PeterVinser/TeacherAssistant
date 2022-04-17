package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.main.NewHomeworkFragmentViewModel

class NewHomeworkFragmentViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewHomeworkFragmentViewModel::class.java)) {
            return NewHomeworkFragmentViewModel() as T
        }
        throw IllegalArgumentException("View model not found")
    }
}