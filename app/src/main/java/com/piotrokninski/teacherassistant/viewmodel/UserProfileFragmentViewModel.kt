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
import com.piotrokninski.teacherassistant.model.contract.FirestoreFriendContract
import com.piotrokninski.teacherassistant.repository.FirestoreFriendInvitationRepository
import com.piotrokninski.teacherassistant.repository.FirestoreFriendRepository
import com.piotrokninski.teacherassistant.repository.FirestoreUserRepository
import kotlinx.coroutines.launch

class UserProfileFragmentViewModel(private val searchedUserId: String): ViewModel(), Observable {

    private val TAG = "UserProfileFragmentView"

    //Needed when user is invited but did not respond yet
    private var friendInvitation: FriendInvitation? = null

    //Needed when user is already a friend
    private var friend: Friend? = null

    //The user that is currently logged in
    private lateinit var currentUser: User

    //The user that has been searched
    private val _user = MutableLiveData<User>()
    @Bindable
    val user: LiveData<User> = _user

    private val _friendStatus = MutableLiveData<String>()
    val friendStatus: LiveData<String> = _friendStatus

    init {
        viewModelScope.launch {
            _user.value = FirestoreUserRepository.getUserDataOnce(searchedUserId)

            currentUser = FirestoreUserRepository.getUserDataOnce(FirebaseAuth.getInstance().currentUser!!.uid)!!

            friendInvitation = FirestoreFriendInvitationRepository.getFriendInvitationDataOnce(currentUser.userId, searchedUserId)
            friend = FirestoreFriendRepository.getFriendDataOnce(currentUser.userId, searchedUserId)

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

            FriendInvitation.deleteInvitation(currentUser.userId, user.value!!.userId)

            updateFriendStatus()
        }
    }

    fun sendInvitation(invitationType: String, invitationMessage: String?) {

        val invitation = FriendInvitation(currentUser.userId, currentUser.fullName,
            user.value!!.userId, user.value!!.fullName, invitationType, invitationMessage, null)

        FriendInvitation.sendInvitation(invitation)

        updateFriendStatus()
    }

    //Used when the detailed invitation is required (moving to the InvitationDetailsFragment)
    fun prepareInvitation(invitationType: String, invitationMessage: String?): FriendInvitation {

        return FriendInvitation(currentUser.userId, currentUser.fullName,
            user.value!!.userId, user.value!!.fullName, invitationType, invitationMessage, null)
    }

    private suspend fun approveInvitation() {

        Log.d(TAG, "approveInvitation: called")

        FriendInvitation.approveInvitation(friendInvitation!!)

        friend = FirestoreFriendRepository.getFriendDataOnce(currentUser.userId, user.value!!.userId)

        updateFriendStatus()
    }

    private fun cancelInvitation() {
        FriendInvitation.deleteInvitation(user.value!!.userId, currentUser.userId)
    }

    private fun deleteFriend() {

        Log.d(TAG, "deleteFriend: called")
        
        //Deleting the friend in current user collection
        Friend.deleteFriend(currentUser.userId, user.value!!.userId)

        //Deleting the current user as friend in friend's collection
        Friend.deleteFriend(user.value!!.userId, currentUser.userId)

        friend = null
    }

    private fun blocked() {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }
}