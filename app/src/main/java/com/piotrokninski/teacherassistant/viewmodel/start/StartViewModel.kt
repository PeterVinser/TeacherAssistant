package com.piotrokninski.teacherassistant.viewmodel.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.model.user.UserNotificationSettings
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StartViewModel(private val dataStoreRepository: DataStoreRepository): ViewModel() {

    private val userRepository: RoomUserRepository

    private val eventChannel = Channel<StartEvent>()
    val eventFlow = eventChannel.receiveAsFlow()

    init {
        val userDao = AppDatabase.getInstance().userDao
        userRepository = RoomUserRepository(userDao)
    }

    fun initApp(user: User?, userId: String) {
        viewModelScope.launch {
            val signedUser = user ?: FirestoreUserRepository.getUserDataOnce(userId)

            if (signedUser != null) userRepository.insertUser(signedUser)

            if (signedUser != null) {
                if (signedUser.student) {
                    dataStoreRepository.putString(DataStoreRepository.Constants.VIEW_TYPE, AppConstants.VIEW_TYPE_STUDENT)

                    eventChannel.send(StartEvent.SignedEvent)
                    return@launch
                }

                if (signedUser.tutor) {
                    dataStoreRepository.putString(DataStoreRepository.Constants.VIEW_TYPE, AppConstants.VIEW_TYPE_TUTOR)

                    eventChannel.send(StartEvent.SignedEvent)
                    return@launch
                }
            }
        }
    }

    fun setNotifications(userId: String, deviceAvailable: Boolean) {
        viewModelScope.launch {
            UserNotificationSettings.setDeviceNotificationToken(userId, deviceAvailable)
        }
    }

    sealed class StartEvent {
        object SignedEvent : StartEvent()
    }

    class Factory(private val dataStoreRepository: DataStoreRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StartViewModel::class.java)) {
                return StartViewModel(dataStoreRepository) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}