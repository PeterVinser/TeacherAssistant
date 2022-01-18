package com.piotrokninski.teacherassistant.viewmodel.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piotrokninski.teacherassistant.model.user.UserNotificationSettings
import kotlinx.coroutines.launch

class StartActivityViewModel: ViewModel() {

    fun setNotifications(userId: String, deviceAvailable: Boolean) {
        viewModelScope.launch {
            UserNotificationSettings.setDeviceNotificationToken(userId, deviceAvailable)
        }
    }
}