package com.piotrokninski.teacherassistant.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.Friend
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import kotlinx.coroutines.launch

class ContactsFragmentViewModel : ViewModel() {
    private val TAG = "ContactsFragmentViewMod"

    private val _friends = MutableLiveData<List<Friend>>()
    val friends: LiveData<List<Friend>> = _friends

    init {
        getFriends()
    }

    fun getFriends() {
        viewModelScope.launch {
            _friends.value = FirestoreFriendRepository.getFriends(
                FirebaseAuth.getInstance().currentUser!!.uid,
                FirestoreFriendContract.STATUS_APPROVED
            )

            _friends.value!!.forEach {
                Log.d(TAG, "getFriends: ${it.fullName}")
            }
        }
    }
}
