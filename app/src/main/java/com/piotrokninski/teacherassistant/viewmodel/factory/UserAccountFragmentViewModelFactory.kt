package com.piotrokninski.teacherassistant.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.UserAccountFragmentViewModel

class UserAccountFragmentViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserAccountFragmentViewModel::class.java)) {
            return UserAccountFragmentViewModel() as T
        }
        throw IllegalArgumentException("View model not found")
    }
}