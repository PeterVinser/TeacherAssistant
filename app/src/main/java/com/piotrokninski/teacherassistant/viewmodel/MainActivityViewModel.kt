package com.piotrokninski.teacherassistant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.User
import com.piotrokninski.teacherassistant.repository.FirestoreUserRepository
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    val viewType = MutableLiveData<String>()

    private lateinit var user: User

    private val _userFullName = MutableLiveData<String>()
    val userFullName: LiveData<String> = _userFullName

    private val _userType = MutableLiveData<String>()
    val userType: LiveData<String> = _userType

    init {
        viewModelScope.launch {
            user = FirestoreUserRepository.getUserDataOnce(FirebaseAuth.getInstance().currentUser!!.uid)!!

            _userFullName.value = user.fullName

            _userType.value = user.getProfession()
        }
    }
}