package com.piotrokninski.teacherassistant.viewmodel

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.Friend
import com.piotrokninski.teacherassistant.model.FriendInvitation
import com.piotrokninski.teacherassistant.model.User
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendInvitationRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import kotlinx.coroutines.launch

class UserProfileFragmentViewModel(private val searchedUserId: String): ViewModel(), Observable {

    private val TAG = "UserProfileFragmentView"
    
    private var userRepository: RoomUserRepository

    //Needed when user is invited but did not respond yet
    private var friendInvitation: FriendInvitation? = null

    //Needed when user is already a friend
    private var friend: Friend? = null

    //The user that is currently logged in
    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var currentUser: User

    //The user that has been searched
    private val _user = MutableLiveData<User>()
    @Bindable
    val user: LiveData<User> = _user

    private val _friendStatus = MutableLiveData<String>()
    val friendStatus: LiveData<String> = _friendStatus

    init {
        
        val userDao = AppDatabase.getInstance().userDao
        userRepository = RoomUserRepository(userDao)
        
        viewModelScope.launch {
            _user.value = FirestoreUserRepository.getUserDataOnce(searchedUserId)

            currentUser = userRepository.getUser(currentUserId)!!

            Log.d(TAG, "CurrentUser: ${currentUser.fullName}")

            friendInvitation = FirestoreFriendInvitationRepository.getFriendInvitationDataOnce(currentUserId, searchedUserId)
            friend = FirestoreFriendRepository.getFriendDataOnce(currentUserId, searchedUserId)

            updateFriendStatus()
        }
    }

    private fun updateFriendStatus() {
        if (friend == null) {
            _friendStatus.value = FirestoreFriendContract.STATUS_BLANK
        } else {
            _friendStatus.value = friend!!.status
        }
    }

    fun onInviteButtonClicked() {
        viewModelScope.launch {

            when (friendStatus.value) {
                FirestoreFriendContract.STATUS_INVITED -> cancelInvitation()

                FirestoreFriendContract.STATUS_INVITING -> approveInvitation()

                FirestoreFriendContract.STATUS_APPROVED -> deleteFriend()

                FirestoreFriendContract.STATUS_BLOCKED -> blocked()
            }

            updateFriendStatus()
        }
    }

    fun rejectInvitation() {
        if (friendStatus.value == FirestoreFriendContract.STATUS_INVITING) {

            FriendInvitation.deleteInvitation(currentUserId, user.value!!.userId)

            updateFriendStatus()
        }
    }

    fun sendInvitation(invitationType: String, invitationMessage: String?) {

        val invitation = FriendInvitation(currentUserId, currentUser.fullName,
            user.value!!.userId, user.value!!.fullName, invitationType, invitationMessage, null)

        FriendInvitation.sendInvitation(invitation)

        updateFriendStatus()
    }

    //Used when the detailed invitation is required (moving to the InvitationDetailsFragment)
    fun prepareInvitation(invitationType: String, invitationMessage: String?): FriendInvitation {

        return FriendInvitation(currentUserId, currentUser.fullName,
            user.value!!.userId, user.value!!.fullName, invitationType, invitationMessage, null)
    }

    private suspend fun approveInvitation() {

        Log.d(TAG, "approveInvitation: called")

        FriendInvitation.approveInvitation(friendInvitation!!)

        friend = FirestoreFriendRepository.getFriendDataOnce(currentUserId, user.value!!.userId)

        updateFriendStatus()
    }

    private fun cancelInvitation() {
        FriendInvitation.deleteInvitation(user.value!!.userId, currentUserId)
    }

    private fun deleteFriend() {

        Log.d(TAG, "deleteFriend: called")
        
        //Deleting the friend in current user collection
        Friend.deleteFriend(currentUserId, user.value!!.userId)

        //Deleting the current user as friend in friend's collection
        Friend.deleteFriend(user.value!!.userId, currentUserId)

        friend = null
    }

    private fun blocked() {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}