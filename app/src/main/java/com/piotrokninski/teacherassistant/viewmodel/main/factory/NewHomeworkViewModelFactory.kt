package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.main.NewHomeworkViewModel

class NewHomeworkViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewHomeworkViewModel::class.java)) {
            return NewHomeworkViewModel() as T
        }
        throw IllegalArgumentException("View model not found")
    }
}