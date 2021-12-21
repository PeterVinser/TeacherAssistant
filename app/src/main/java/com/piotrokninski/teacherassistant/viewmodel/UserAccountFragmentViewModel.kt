package com.piotrokninski.teacherassistant.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.repository.FirestoreUserRepository
import com.piotrokninski.teacherassistant.model.User
import kotlinx.coroutines.launch

class UserAccountFragmentViewModel: ViewModel(), Observable {

    //TODO inspect the usage of mutable and live data and its accessibility
    val editing = MutableLiveData<Boolean>()

    @Bindable
    val user = MutableLiveData<User>()

    //TODO add the check whether the text has changed or not before calling the database
//    fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//        Log.d("TAG", "onTextChanged: the text has changed")
//    }


    init {
        viewModelScope.launch {
            user.value = FirestoreUserRepository.getUserDataOnce(FirebaseAuth.getInstance().currentUser!!.uid)
        }
        editing.value = false
    }

    fun updateUserData() {
        FirestoreUserRepository.setUserData(user.value!!)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}