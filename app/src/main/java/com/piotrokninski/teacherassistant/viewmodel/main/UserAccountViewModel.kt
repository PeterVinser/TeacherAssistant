package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.datastore.DataStoreRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class UserAccountViewModel(private val dataStoreRepository: DataStoreRepository): ViewModel(), Observable {

    private val userRepository: RoomUserRepository

    val editing = MutableLiveData<Boolean>()

    private val _viewType = MutableLiveData<String>()
    val viewType: LiveData<String> = _viewType

    @Bindable
    val user = MutableLiveData<User>()

    init {
        val userDao = AppDatabase.getInstance().userDao
        userRepository = RoomUserRepository(userDao)

        viewModelScope.launch {
            _viewType.value = dataStoreRepository.getString(DataStoreRepository.Constants.VIEW_TYPE)

            user.value = FirestoreUserRepository.getUserDataOnce(FirebaseAuth.getInstance().currentUser!!.uid)

            if (user.value != null) {
                userRepository.updateUser(user.value!!)
            }
        }

        editing.value = false
    }

    fun updateUserData() {
        FirestoreUserRepository.setUserData(user.value!!)
    }

    fun updateViewType(viewType: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val completed = dataStoreRepository.putString(DataStoreRepository.Constants.VIEW_TYPE, viewType)

            _viewType.value = viewType

            if (!completed) cancel()
        }.invokeOnCompletion {
            onComplete()
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    class Factory(private val dataStore: DataStoreRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserAccountViewModel::class.java)) {
                return UserAccountViewModel(dataStore) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}