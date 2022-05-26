package com.piotrokninski.teacherassistant.viewmodel.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.viewmodel.main.ChatFragmentViewModel
import com.piotrokninski.teacherassistant.viewmodel.main.CourseDetailsFragmentViewModel

class ChatFragmentViewModelFactory(private val friend: Friend): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatFragmentViewModel::class.java)) {
            return ChatFragmentViewModel(friend) as T
        }
        throw IllegalArgumentException("View model not found")
    }
}