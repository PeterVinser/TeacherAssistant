package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation.Companion.toFriendInvitation
import kotlinx.coroutines.tasks.await

object FirestoreFriendInvitationRepository {
    private const val TAG = "FirestoreFriendInvitati"

    fun addFriendInvitation(friendInvitation: FriendInvitation) {
        val db = FirebaseFirestore.getInstance()

        val document = db.collection(FirestoreFriendInvitationContract.COLLECTION_NAME).document()
        friendInvitation.invitationId = document.id

        document.set(friendInvitation)
    }

    fun updateFriendInvitation(invitationId: String, field: String, value: Any) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreFriendInvitationContract.COLLECTION_NAME).document(invitationId)
            .update(field, value)
    }

    fun deleteFriendInvitation(invitationId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreFriendInvitationContract.COLLECTION_NAME).document(invitationId)
            .delete()
    }

    suspend fun getFriendInvitation(invitationId: String): FriendInvitation? {
        val db = FirebaseFirestore.getInstance()

        val ref =
            db.collection(FirestoreFriendInvitationContract.COLLECTION_NAME).document(invitationId)

        return try {
            ref.get().await().toFriendInvitation()
        } catch (e: Exception) {
            Log.e(TAG, "getInvitation: ", e)
            null
        }
    }

    suspend fun getReceivedFriendsInvitations(userId: String): ArrayList<FriendInvitation>? {
        val db = FirebaseFirestore.getInstance()

        val friendInvitations = ArrayList<FriendInvitation>()

        val ref = db.collection(FirestoreFriendInvitationContract.COLLECTION_NAME)

        val query = ref.whereEqualTo(FirestoreFriendInvitationContract.INVITED_USER_ID, userId)

        return try {
            query.get().await().forEach { invitation ->
                invitation?.toFriendInvitation()?.let { friendInvitations.add(it) }
            }
            friendInvitations
        } catch (e: Exception) {
            Log.e(TAG, "getFriendsInvitations: ", e)
            null
        }
    }

    suspend fun getSentFriendInvitations(userId: String): ArrayList<FriendInvitation>? {
        val db = FirebaseFirestore.getInstance()

        val friendInvitations = ArrayList<FriendInvitation>()

        val ref = db.collection(FirestoreFriendInvitationContract.COLLECTION_NAME)

        val query = ref.whereEqualTo(FirestoreFriendInvitationContract.INVITING_USER_ID, userId)

        return try {
            query.get().await().forEach { invitation ->
                invitation?.toFriendInvitation()?.let { friendInvitations.add(it) }
            }
            friendInvitations
        } catch (e: Exception) {
            Log.e(TAG, "getSentFriendInvitations: ", e)
            null
        }
    }
}