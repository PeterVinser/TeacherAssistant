package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.adapteritem.ContactAdapterItem
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendInvitationRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMessageRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.launch

class ContactsFragmentViewModel : ViewModel() {
    private val TAG = "ContactsFragmentViewMod"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val viewType = MainPreferences.getViewType()

    private val _contactItems = MutableLiveData<List<ContactAdapterItem.FriendAdapterItem>>()
    val contactAdapterItems: LiveData<List<ContactAdapterItem.FriendAdapterItem>> = _contactItems

    init {
        getFriends()
    }

    fun getFriends() {
        viewModelScope.launch {
//            val auxList = ArrayList<ContactAdapterItem>()

            val friendItems = ArrayList<ContactAdapterItem.FriendAdapterItem>()

            // TODO: delete the pure friendship type

            val friends = when(viewType) {
                AppConstants.VIEW_TYPE_STUDENT -> FirestoreFriendRepository.getApprovedFriends(
                    currentUserId,
                    Friend.TYPE_TUTOR
                )
                AppConstants.VIEW_TYPE_TUTOR -> FirestoreFriendRepository.getApprovedFriends(
                    currentUserId,
                    Friend.TYPE_STUDENT
                )

                else -> throw IllegalArgumentException("Unknown viewType")
            }

            friends.forEach { friend ->
                val latestMessage = FirestoreMessageRepository.getLatestMessage(friend.chatId)
                val read = latestMessage?.read?.let {
                    it && latestMessage.senderId != currentUserId
                } ?: true
                friendItems.add(ContactAdapterItem.FriendAdapterItem(friend, latestMessage, read))
            }

            _contactItems.value = friendItems
        }
    }
}
