package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.meeting.MeetingInvitation
import com.piotrokninski.teacherassistant.model.meeting.MeetingInvitation.Companion.toMeetingInvitation
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object FirestoreMeetingInvitationRepository {
    private const val TAG = "FirestoreMeetingInvitat"

    fun addMeetingInvitation(meetingInvitation: MeetingInvitation) {
        val db = FirebaseFirestore.getInstance()

        db.collection(MeetingInvitation.COLLECTION_NAME).add(meetingInvitation)
    }

    fun updateMeetingInvitation(id: String, meetingInvitation: MeetingInvitation) {
        val db = FirebaseFirestore.getInstance()

        db.collection(MeetingInvitation.COLLECTION_NAME).document(id).set(
            meetingInvitation
        )
    }

    fun updateMeetingInvitation(id: String, field: String, value: Any) {
        val db = FirebaseFirestore.getInstance()

        db.collection(MeetingInvitation.COLLECTION_NAME).document(id).update(
            field, value
        )
    }

    fun deleteMeetingInvitation(id: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(MeetingInvitation.COLLECTION_NAME).document(id)
            .delete()
    }

    suspend fun getReceivedMeetingInvitations(userId: String, status: String): ArrayList<MeetingInvitation>? {
        val db = FirebaseFirestore.getInstance()

        val ref = db.collection(MeetingInvitation.COLLECTION_NAME)

        val query = ref.whereEqualTo(MeetingInvitation.INVITED_USER_ID, userId)
            .whereEqualTo(MeetingInvitation.STATUS, status)

        return try {
            val meetings = ArrayList<MeetingInvitation>()
            query.get().await().forEach { invitation ->
                invitation.toMeetingInvitation()?.let {
                    it.id = invitation.id
                    meetings.add(it)
                }
            }

            meetings.ifEmpty { null }
        } catch (e: Exception) {
            Log.e(TAG, "getReceivedMeetingInvitations: ", e)
            null
        }
    }

    suspend fun getSentMeetingInvitations(userId: String, status: String): ArrayList<MeetingInvitation>? {
        val db = FirebaseFirestore.getInstance()

        val ref = db.collection(MeetingInvitation.COLLECTION_NAME)

        val query = ref.whereEqualTo(MeetingInvitation.INVITING_USER_ID, userId)
            .whereEqualTo(MeetingInvitation.STATUS, status)

        return try {
            val meetings = ArrayList<MeetingInvitation>()
            query.get().await().forEach { invitation ->
                invitation.toMeetingInvitation()?.let {
                    it.id = invitation.id
                    meetings.add(it)
                }
            }
            meetings.ifEmpty { null }
        } catch (e: Exception) {
            Log.e(TAG, "getSentMeetingInvitations: ", e)
            null
        }
    }
}