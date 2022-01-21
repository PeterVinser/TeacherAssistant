package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation.Companion.toFriendInvitation
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreUserContract
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.friend.Friend.Companion.toFriend
import kotlinx.coroutines.tasks.await

object FirestoreFriendInvitationRepository {
    private const val TAG = "FirestoreFriendInvitati"

    suspend fun getFriendInvitationDataOnce(
        invitedUserId: String,
        invitingUserId: String
    ): FriendInvitation? {
        val db = FirebaseFirestore.getInstance()

        val friendsInvitationsCollectionRef =
            db.collection(FirestoreUserContract.COLLECTION_NAME).document(invitedUserId)
                .collection(FirestoreFriendInvitationContract.COLLECTION_NAME)
                .document(invitingUserId)

        return try {
            friendsInvitationsCollectionRef.get().await().toFriendInvitation()
        } catch (e: Exception) {
            Log.e(TAG, "getFriendInvitationDataOnce: ", e)
            null
        }
    }

    suspend fun getAllFriendsInvitations(userId: String): ArrayList<FriendInvitation> {
        val db = FirebaseFirestore.getInstance()

        //TODO change the return to return try and add nullable return type
        val friendsInvitations = ArrayList<FriendInvitation>()

        val friendsInvitationsCollectionRef =
            db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
                .collection(FirestoreFriendInvitationContract.COLLECTION_NAME)

        try {
            friendsInvitationsCollectionRef.get().await().forEach { invitation ->
                invitation?.toFriendInvitation()?.let { friendsInvitations.add(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllFriendsInvitations: ", e)
        }

        return friendsInvitations
    }

    suspend fun getReceivedFriendsInvitations(userId: String): ArrayList<FriendInvitation> {
        val db = FirebaseFirestore.getInstance()

        val friendInvitations = ArrayList<FriendInvitation>()

        val friendInvitationsCollectionRef =
            db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
                .collection(FirestoreFriendInvitationContract.COLLECTION_NAME)

        try {
            friendInvitationsCollectionRef.get().await().forEach { invitation ->
                invitation?.toFriendInvitation()?.let { friendInvitations.add(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getFriendsInvitations: ", e)
        }

        return friendInvitations
    }

    suspend fun getSentFriendInvitations(userId: String): ArrayList<FriendInvitation> {
        val db = FirebaseFirestore.getInstance()

        val friendInvitations = ArrayList<FriendInvitation>()

        val invitedFriends = ArrayList<Friend>()

        val friendsCollectionRef =
            db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
                .collection(FirestoreFriendContract.COLLECTION_NAME)

        val invitedFriendsQuery = friendsCollectionRef.whereEqualTo(
            FirestoreFriendContract.STATUS,
            FirestoreFriendContract.STATUS_INVITED
        )

        try {
            invitedFriendsQuery.get().await().forEach { friend ->
                friend?.toFriend()?.let { invitedFriends.add(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getSentFriendInvitations: ", e)
        }

        if (invitedFriends.isNotEmpty()) {

            invitedFriends.forEach { friend ->
                val friendInvitationsRef = db.collection(FirestoreUserContract.COLLECTION_NAME).document(friend.userId)
                    .collection(FirestoreFriendInvitationContract.COLLECTION_NAME).document(userId)

                val invitation = try {
                    friendInvitationsRef.get().await().toFriendInvitation()
                } catch (e: Exception) {
                    Log.e(TAG, "getSentFriendInvitations: ", e)
                    null
                }

                if (invitation != null) {
                    friendInvitations.add(invitation)
                }
            }
        }

        return friendInvitations
    }
}