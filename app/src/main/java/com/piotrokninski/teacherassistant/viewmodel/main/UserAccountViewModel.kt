package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import kotlinx.coroutines.launch

class UserAccountViewModel: ViewModel(), Observable {

    private val userRepository: RoomUserRepository

    val editing = MutableLiveData<Boolean>()

    private val _viewType = MutableLiveData<String>()
    val viewType: LiveData<String> = _viewType

    @Bindable
    val user = MutableLiveData<User>()

    //TODO add the check whether the text has changed or not before calling the database
//    fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//        Log.d("TAG", "onTextChanged: the text has changed")
//    }


    init {
        val userDao = AppDatabase.getInstance().userDao
        userRepository = RoomUserRepository(userDao)

        _viewType.value = MainPreferences.getViewType()

        viewModelScope.launch {
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

    fun updateViewType(viewType: String) {
        MainPreferences.updateViewType(viewType)

        _viewType.value = viewType
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    class Factory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserAccountViewModel::class.java)) {
                return UserAccountViewModel() as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}