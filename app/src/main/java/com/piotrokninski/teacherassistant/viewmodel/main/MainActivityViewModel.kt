package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.model.user.UserHint
import com.piotrokninski.teacherassistant.model.user.UserNotificationSettings
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserHintRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    private val TAG = "MainActivityViewModel"

    private var userRepository: RoomUserRepository

    private val _viewType = MutableLiveData<String>()
    val viewType: LiveData<String> = _viewType

    init {

        val userDao = AppDatabase.getInstance().userDao
        userRepository = RoomUserRepository(userDao)

        viewModelScope.launch {
            _viewType.value = MainPreferences.getViewType()

            Log.d(TAG, "mainViewModel: view type is ${viewType.value}")

            saveCurrentUserData()
        }
    }

    fun initUser(user: User) {
        viewModelScope.launch {

            val userHint = UserHint.createHint(user.userId, user.fullName)

            FirestoreUserRepository.setUserData(user)
            FirestoreUserHintRepository.setUserHintData(userHint)

            userRepository.insertUser(user)
        }
    }

    fun setNotifications(userId: String, deviceAvailable: Boolean) {
        viewModelScope.launch {
            UserNotificationSettings.setDeviceNotificationToken(userId, deviceAvailable)
        }
    }

    private suspend fun saveCurrentUserData() {
        val currentUser = FirestoreUserRepository.getUserDataOnce(FirebaseAuth.getInstance().currentUser!!.uid)

        if (currentUser != null) {
            userRepository.insertUser(currentUser)
        }
    }


    fun updateViewType(viewType: String) {
        MainPreferences.updateViewType(viewType)

        _viewType.value = viewType
    }
}