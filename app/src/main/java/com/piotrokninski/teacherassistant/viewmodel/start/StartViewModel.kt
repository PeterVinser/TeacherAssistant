package com.piotrokninski.teacherassistant.viewmodel.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.piotrokninski.teacherassistant.model.user.UserNotificationSettings
import kotlinx.coroutines.launch

class StartViewModel: ViewModel() {

    fun setNotifications(userId: String, deviceAvailable: Boolean) {
        viewModelScope.launch {
            UserNotificationSettings.setDeviceNotificationToken(userId, deviceAvailable)
        }
    }

    class Factory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StartViewModel::class.java)) {
                return StartViewModel() as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}