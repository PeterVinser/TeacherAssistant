package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.main.HomeworkFragmentViewModel

class HomeworkFragmentViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeworkFragmentViewModel::class.java)) {
            return HomeworkFragmentViewModel() as T
        }
        throw IllegalArgumentException("View model not found")
    }
}