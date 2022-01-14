package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation.Companion.toFriendInvitation
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreUserContract
import kotlinx.coroutines.tasks.await

object FirestoreFriendInvitationRepository {
    private const val TAG = "FirestoreFriendInvitati"

    fun setFriendInvitationData(friendInvitation: FriendInvitation) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreUserContract.COLLECTION_NAME).document(friendInvitation.invitedUserId)
            .collection(FirestoreFriendInvitationContract.COLLECTION_NAME).document(friendInvitation.invitingUserId)
            .set(friendInvitation)
    }

    fun deleteFriendInvitationData(invitedFriendId: String, invitingFriendId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreUserContract.COLLECTION_NAME).document(invitedFriendId)
            .collection(FirestoreFriendInvitationContract.COLLECTION_NAME).document(invitingFriendId)
            .delete()
    }

    suspend fun getFriendInvitationDataOnce(invitedUserId: String, invitingUserId: String): FriendInvitation? {
        val db = FirebaseFirestore.getInstance()

        val friendsInvitationsCollectionRef = db.collection(FirestoreUserContract.COLLECTION_NAME).document(invitedUserId)
            .collection(FirestoreFriendInvitationContract.COLLECTION_NAME).document(invitingUserId)

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

        val friendsInvitationsCollectionRef = db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
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

    suspend fun getFriendsInvitations(userId: String): ArrayList<FriendInvitation> {
        val db = FirebaseFirestore.getInstance()

        val friendsInvitations = ArrayList<FriendInvitation>()

        val friendsInvitationsCollectionRef = db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
            .collection(FirestoreFriendInvitationContract.COLLECTION_NAME)

        try {
            friendsInvitationsCollectionRef.get().await().forEach { invitation ->
                invitation?.toFriendInvitation()?.let { friendsInvitations.add(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getFriendsInvitations: ", e)
        }

        return friendsInvitations
    }
}