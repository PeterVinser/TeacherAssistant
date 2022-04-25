package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.adapteritem.ContactAdapterItem
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendInvitationRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.launch

class ContactsFragmentViewModel : ViewModel() {
    private val TAG = "ContactsFragmentViewMod"

    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val _contactItems = MutableLiveData<List<ContactAdapterItem>>()
    val mContactAdapterItems: LiveData<List<ContactAdapterItem>> = _contactItems

    init {
        getFriends()
    }

    fun getFriends() {
        viewModelScope.launch {
            val auxList = ArrayList<ContactAdapterItem>()

            val studentsHeader = ContactAdapterItem.HeaderAdapterItem(R.string.header_item_students)

            val students = ArrayList<ContactAdapterItem>()

            FirestoreFriendRepository.getApprovedFriends(
                currentUserId,
                FirestoreFriendContract.TYPE_STUDENT
            ).forEach { friend ->
                val studentItem =
                    ContactAdapterItem.FriendAdapterItem(friend.userId, friend.fullName)
                students.add(studentItem)
            }

            val tutorsHeader = ContactAdapterItem.HeaderAdapterItem(R.string.header_item_tutors)

            val tutors = ArrayList<ContactAdapterItem>()

            FirestoreFriendRepository.getApprovedFriends(
                currentUserId,
                FirestoreFriendContract.TYPE_TUTOR
            ).forEach { friend ->
                val tutorItem = ContactAdapterItem.FriendAdapterItem(friend.userId, friend.fullName)
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

            val friendsHeader = ContactAdapterItem.HeaderAdapterItem(R.string.header_item_friends)

            val friends = ArrayList<ContactAdapterItem>()

            FirestoreFriendRepository.getApprovedFriends(
                currentUserId,
                FirestoreFriendContract.TYPE_FRIEND
            ).forEach { friend ->
                val friendItem =
                    ContactAdapterItem.FriendAdapterItem(friend.userId, friend.fullName)
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
            val auxList = ArrayList<ContactAdapterItem>()

            val receivedInvitationsHeader =
                ContactAdapterItem.HeaderAdapterItem(R.string.header_item_received_invitations)

            val receivedInvitations = ArrayList<ContactAdapterItem>()

            FirestoreFriendInvitationRepository.getReceivedFriendsInvitations(currentUserId)
                ?.forEach { invitation ->
                    val receivedInvitationItem = ContactAdapterItem.FriendInvitationAdapterItem(
                        AppConstants.RECEIVED_INVITATIONS,
                        invitation
                    )
                    receivedInvitations.add(receivedInvitationItem)
                }

            val sentInvitationsHeader =
                ContactAdapterItem.HeaderAdapterItem(R.string.header_item_sent_invitations)

            val sentInvitations = ArrayList<ContactAdapterItem>()

            FirestoreFriendInvitationRepository.getSentFriendInvitations(currentUserId)
                ?.forEach { invitation ->
                    val sentInvitationItem = ContactAdapterItem.FriendInvitationAdapterItem(
                        AppConstants.SENT_INVITATIONS,
                        invitation
                    )
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
