package com.piotrokninski.teacherassistant.viewmodel.start.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.start.StartActivityViewModel

class StartActivityViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartActivityViewModel::class.java)) {
            return StartActivityViewModel() as T
        }
        throw IllegalArgumentException("View model not found")
    }
}