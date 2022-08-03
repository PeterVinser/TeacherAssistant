package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreInvitationRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserRepository
import com.piotrokninski.teacherassistant.repository.room.AppDatabase
import com.piotrokninski.teacherassistant.repository.room.repository.RoomUserRepository
import kotlinx.coroutines.launch

class UserProfileViewModel(private val searchedUserId: String) : ViewModel(), Observable {

    private val TAG = "UserProfileFragmentView"

    private val userRepository: RoomUserRepository

    //Needed when user is invited but did not respond yet
    private val _invitation = MutableLiveData<Invitation>()
    val invitation: LiveData<Invitation> = _invitation

    //Needed when user is already a friend
    private var friend: Friend? = null

    //The user that is currently logged in
    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    lateinit var currentUser: User

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

            friend = FirestoreFriendRepository.getFriendDataOnce(currentUserId, searchedUserId)

            _invitation.value = friend?.invitationId?.let {
                FirestoreInvitationRepository.getInvitation(it)
            }

            updateFriendStatus()
        }
    }

    private fun updateFriendStatus() {
        if (friend == null) {
            _friendStatus.value = Friend.Contract.STATUS_BLANK
        } else {
            _friendStatus.value = friend!!.status
        }
    }

    fun onInviteButtonClicked() {
        viewModelScope.launch {

            when (friendStatus.value) {
                Friend.Contract.STATUS_INVITED -> cancelInvitation()

                Friend.Contract.STATUS_INVITING -> approveInvitation()

                Friend.Contract.STATUS_APPROVED -> deleteFriend()

                Friend.Contract.STATUS_BLOCKED -> blocked()
            }

            updateFriendStatus()
        }
    }

    fun sendInvitation(invitedAs: String, invitationMessage: String?) {

        val invitation = Invitation(
            null,
            currentUserId,
            currentUser.fullName,
            searchedUserId,
            user.value!!.fullName,
            invitedAs,
            type = Invitation.Contract.TYPE_FRIENDSHIP,
            message = invitationMessage
        )

        FirestoreInvitationRepository.addInvitation(invitation)

        _invitation.value = invitation

        updateFriendStatus()
    }

    //Used when the detailed invitation is required (moving to the InvitationDetailsFragment)
    fun prepareInvitation(invitedAs: String, invitationMessage: String?): Invitation {

        return Invitation(
            null,
            currentUserId,
            currentUser.fullName,
            searchedUserId,
            user.value!!.fullName,
            invitedAs,
            type = Invitation.Contract.TYPE_FRIENDSHIP,
            message = invitationMessage
        )
    }

    private suspend fun approveInvitation() {

        invitation.value?.id?.let {
            FirestoreInvitationRepository.updateInvitation(
                it,
                Invitation.Contract.STATUS,
                Invitation.Contract.STATUS_APPROVED
            )
        }

        friend = FirestoreFriendRepository.getFriendDataOnce(currentUserId, searchedUserId)

        updateFriendStatus()
    }

    fun rejectInvitation() {
        if (friendStatus.value == Friend.Contract.STATUS_INVITING) {

            invitation.value?.id?.let {
                FirestoreInvitationRepository.updateInvitation(
                    it,
                    Invitation.Contract.STATUS,
                    Invitation.Contract.STATUS_REJECTED
                )
            }

            updateFriendStatus()
        }
    }

    private fun cancelInvitation() {
        invitation.value?.id?.let { FirestoreInvitationRepository.deleteInvitation(it) }
    }

    private fun deleteFriend() {

        Log.d(TAG, "deleteFriend: called")

        friend?.let {
            FirestoreFriendRepository.deleteFriend(currentUserId, it.userId)
            FirestoreFriendRepository.deleteFriend(it.userId, currentUserId)
        }

        friend = null

        updateFriendStatus()
    }

    private fun blocked() {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    }

    class Factory(private val userId: String): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {
                return UserProfileViewModel(userId) as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}