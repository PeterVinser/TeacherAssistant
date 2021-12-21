package com.piotrokninski.teacherassistant.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.viewmodel.UserProfileFragmentViewModel

class UserProfileFragmentViewModelFactory(private val userId: String): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserProfileFragmentViewModel::class.java)) {
            return UserProfileFragmentViewModel(userId) as T
        }
        throw IllegalArgumentException("View model not found")
    }
}