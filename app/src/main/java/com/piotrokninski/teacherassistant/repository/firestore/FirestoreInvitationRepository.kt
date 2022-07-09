package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.Invitation.Companion.toInvitation
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import kotlinx.coroutines.tasks.await

object FirestoreInvitationRepository {
    private const val TAG = "FirestoreInvitationRepo"

    fun addInvitation(invitation: Invitation) {
        val db = FirebaseFirestore.getInstance()

        val document = db.collection(Invitation.Contract.COLLECTION_NAME).document()
        invitation.id = document.id

        document.set(invitation)
    }

    fun updateInvitation(id: String, field: String, value: Any) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Invitation.Contract.COLLECTION_NAME).document(id)
            .update(field, value)
    }

    fun updateInvitation(invitation: Invitation) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Invitation.Contract.COLLECTION_NAME).document(invitation.id!!)
            .set(invitation)
    }

    fun deleteInvitation(id: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Invitation.Contract.COLLECTION_NAME).document(id)
            .delete()
    }

    suspend fun getInvitation(id: String): Invitation? {
        val db = FirebaseFirestore.getInstance()

        val ref = db.collection(Invitation.Contract.COLLECTION_NAME).document(id)

        return try {
            ref.get().await().toInvitation()
        } catch (e: Exception) {
            Log.e(TAG, "getInvitation: ", e)
            null
        }
    }

    private suspend fun getInvitations(field: String, userId: String, status: String): List<Invitation>? {
        val db = FirebaseFirestore.getInstance()

        val ref = db.collection(Invitation.Contract.COLLECTION_NAME)

        val query = ref.whereEqualTo(field, userId)
            .whereEqualTo(Invitation.Contract.STATUS, status)

        return try {
            val invitations = ArrayList<Invitation>()

            query.get().await().forEach { invitation ->
                invitation.toInvitation()?.let {
                    invitations.add(it)
                }
            }

            invitations.ifEmpty { null }
        } catch (e: Exception) {
            Log.e(TAG, "getSentInvitations: ", e)
            null
        }
    }

    suspend fun getSentInvitations(userId: String, status: String) : List<Invitation>? =
        getInvitations(Invitation.Contract.INVITING_USER_ID, userId, status)

    suspend fun getReceivedInvitations(userId: String, status: String) : List<Invitation>? =
        getInvitations(Invitation.Contract.INVITED_USER_ID, userId, status)
}