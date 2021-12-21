package com.piotrokninski.teacherassistant.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.HomeFeedItem
import com.piotrokninski.teacherassistant.repository.FirestoreFriendInvitationRepository
import kotlinx.coroutines.launch

class HomeFragmentViewModel: ViewModel() {
    private val TAG = "HomeFragmentViewModel"

    private val _homeFeedItems = MutableLiveData<List<HomeFeedItem>>()
    val homeFeedItems: LiveData<List<HomeFeedItem>> = _homeFeedItems

    init {
        _homeFeedItems.value = ArrayList()

        getItems()
    }

    fun getItems() {
        viewModelScope.launch {
            val invitingFriends = FirestoreFriendInvitationRepository.getFriendsInvitations(FirebaseAuth.getInstance().currentUser!!.uid)

            val auxList = ArrayList<HomeFeedItem.Invitation>()
            invitingFriends.forEach { friend ->
                auxList.add(HomeFeedItem.Invitation(friend))
                Log.d(TAG, "getItems: ${friend.invitedUserFullName}")
            }

            _homeFeedItems.value = auxList
        }
    }
}