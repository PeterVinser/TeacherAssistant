package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.adapteritem.HomeAdapterItem
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendInvitationRepository
import kotlinx.coroutines.launch

class HomeFragmentViewModel: ViewModel() {
    private val TAG = "HomeFragmentViewModel"

    private val _homeFeedItems = MutableLiveData<List<HomeAdapterItem>>()
    val mHomeAdapterItems: LiveData<List<HomeAdapterItem>> = _homeFeedItems

    init {
        _homeFeedItems.value = ArrayList()

        getItems()
    }

    fun getItems() {
        viewModelScope.launch {
            val invitingFriends = FirestoreFriendInvitationRepository.getReceivedFriendsInvitations(FirebaseAuth.getInstance().currentUser!!.uid)

            val auxList = ArrayList<HomeAdapterItem.Invitation>()
            invitingFriends.forEach { friend ->
                auxList.add(HomeAdapterItem.Invitation(friend))
                Log.d(TAG, "getItems: ${friend.invitedUserFullName}")
            }

            _homeFeedItems.value = auxList
        }
    }
}