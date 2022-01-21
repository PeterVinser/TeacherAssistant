package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.adapteritem.ContactItem
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendInvitationRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.launch

class ContactsFragmentViewModel : ViewModel() {
    private val TAG = "ContactsFragmentViewMod"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val _contactItems = MutableLiveData<List<ContactItem>>()
    val contactItems: LiveData<List<ContactItem>> = _contactItems

    init {
        getFriends()
    }

    fun getFriends() {
        viewModelScope.launch {
            val auxList = ArrayList<ContactItem>()

            val studentsHeader = ContactItem.HeaderItem(R.string.header_item_students)

            val students = ArrayList<ContactItem>()

            FirestoreFriendRepository.getApprovedFriends(currentUserId, FirestoreFriendContract.TYPE_STUDENT).forEach { friend ->
                val studentItem = ContactItem.FriendItem(friend.userId, friend.fullName)
                students.add(studentItem)
            }

            val tutorsHeader = ContactItem.HeaderItem(R.string.header_item_tutors)

            val tutors = ArrayList<ContactItem>()

            FirestoreFriendRepository.getApprovedFriends(currentUserId, FirestoreFriendContract.TYPE_TUTOR).forEach { friend ->
                val tutorItem = ContactItem.FriendItem(friend.userId, friend.fullName)
                tutors.add(tutorItem)
            }


            when (MainPreferences.getViewType()) {
                AppConstants.VIEW_TYPE_TUTOR -> {
                    if (students.isNotEmpty()) {
                        auxList.add(studentsHeader)
                        auxList.addAll(students)
                    }

                    if (tutors.isNotEmpty()) {
                        auxList.add(tutorsHeader)
                        auxList.addAll(tutors)
                    }
                }

                AppConstants.VIEW_TYPE_STUDENT -> {
                    if (tutors.isNotEmpty()) {
                        auxList.add(tutorsHeader)
                        auxList.addAll(tutors)
                    }

                    if (students.isNotEmpty()) {
                        auxList.add(studentsHeader)
                        auxList.addAll(students)
                    }
                }
            }

            val friendsHeader = ContactItem.HeaderItem(R.string.header_item_friends)

            val friends = ArrayList<ContactItem>()

            FirestoreFriendRepository.getApprovedFriends(currentUserId, FirestoreFriendContract.TYPE_FRIEND).forEach { friend ->
                val friendItem = ContactItem.FriendItem(friend.userId, friend.fullName)
                friends.add(friendItem)
            }

            if (friends.isNotEmpty()) {
                auxList.add(friendsHeader)
                auxList.addAll(friends)
            }

            _contactItems.value = auxList
        }
    }

    fun getInvitations() {
        viewModelScope.launch {
            val auxList = ArrayList<ContactItem>()

            val receivedInvitationsHeader = ContactItem.HeaderItem(R.string.header_item_received_invitations)

            val receivedInvitations = ArrayList<ContactItem>()

            FirestoreFriendInvitationRepository.getReceivedFriendsInvitations(currentUserId).forEach { invitation ->
                val receivedInvitationItem = ContactItem.FriendInvitationItem(AppConstants.RECEIVED_INVITATIONS, invitation)
                receivedInvitations.add(receivedInvitationItem)
            }

            val sentInvitationsHeader = ContactItem.HeaderItem(R.string.header_item_sent_invitations)

            val sentInvitations = ArrayList<ContactItem>()

            FirestoreFriendInvitationRepository.getSentFriendInvitations(currentUserId).forEach { invitation ->
                val sentInvitationItem = ContactItem.FriendInvitationItem(AppConstants.SENT_INVITATIONS, invitation)
                sentInvitations.add(sentInvitationItem)
            }

            if (receivedInvitations.isNotEmpty()) {
                auxList.add(receivedInvitationsHeader)
                auxList.addAll(receivedInvitations)
            }

            if (sentInvitations.isNotEmpty()) {
                auxList.add(sentInvitationsHeader)
                auxList.addAll(sentInvitations)
            }

            _contactItems.value = auxList
        }
    }
}
