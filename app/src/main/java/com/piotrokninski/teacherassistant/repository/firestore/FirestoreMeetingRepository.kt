package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.piotrokninski.teacherassistant.model.Meeting
import com.piotrokninski.teacherassistant.model.Meeting.Companion.toMeeting
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

object FirestoreMeetingRepository {
    private const val TAG = "FirestoreMeetingReposit"

    fun addMeeting(meeting: Meeting) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Meeting.Contract.COLLECTION_NAME).add(meeting)
    }

    suspend fun getMeetings(userId: String): ArrayList<Meeting>? {
        val db = FirebaseFirestore.getInstance()

        val meetingsRef = db.collection(Meeting.Contract.COLLECTION_NAME)

        val query = meetingsRef
            .whereArrayContains(Meeting.Contract.ATTENDEE_IDS, userId)
            .orderBy(Meeting.Contract.DATE, Query.Direction.ASCENDING)

        return try {
            val meetings = ArrayList<Meeting>()

            query.get().await().forEach { meeting ->
                meeting?.toMeeting()?.let { meetings.add(it) }
            }

            if (meetings.isEmpty()) {
                null
            } else {
                meetings
            }
        } catch (e: Exception) {
            Log.e(TAG, "getStudentMeetings: ", e)
            null
        }
    }

    suspend fun getMeetingsFromRange(userId: String, startDate: Date, endDate: Date): ArrayList<Meeting>? {
        val db = FirebaseFirestore.getInstance()

        val meetingsRef = db.collection(Meeting.Contract.COLLECTION_NAME)

        val query = meetingsRef.whereArrayContains(Meeting.Contract.ATTENDEE_IDS, userId)
            .whereGreaterThanOrEqualTo(Meeting.Contract.DATE, startDate)
            .whereLessThanOrEqualTo(Meeting.Contract.DATE, endDate)
            .orderBy(Meeting.Contract.DATE, Query.Direction.ASCENDING)

        return try {
            val meetings = ArrayList<Meeting>()

            query.get().await().forEach { meeting ->
                meeting?.toMeeting()?.let {
                    meetings.add(it)
                }
            }

            meetings.ifEmpty { null }
        } catch (e: Exception) {
            Log.e(TAG, "getUpcomingWeekMeetings: ", e)
             null
        }
    }

    suspend fun getUpcomingMeetings(userId: String): ArrayList<Meeting>? {
        val db = FirebaseFirestore.getInstance()

        val meetingsQuery = db.collection(Meeting.Contract.COLLECTION_NAME)
            .whereArrayContains(Meeting.Contract.ATTENDEE_IDS, userId)
            .whereEqualTo(Meeting.Contract.COMPLETED, false)

        return try {
            val meetings = ArrayList<Meeting>()

            meetingsQuery.get().await().forEach { meeting ->
                meeting?.toMeeting()?.let {
                    it.id = meeting.id
                    meetings.add(it)
                }
            }

            meetings.ifEmpty { null }
        } catch (e: Exception) {
            Log.e(TAG, "getUpcomingSingularMeetings: ", e)
            null
        }
    }

    /**
     * Experimental listener instead of traditional querying
     */
    fun addSingularMeetingsListener(userId: String): Flow<ArrayList<Meeting>?> {
        val db = FirebaseFirestore.getInstance()

        val meetingsQuery = db.collection(Meeting.Contract.COLLECTION_NAME)
            .whereArrayContains(Meeting.Contract.ATTENDEE_IDS, userId)
            .whereEqualTo(Meeting.Contract.SINGULAR, true)
            .whereEqualTo(Meeting.Contract.COMPLETED, false)

        return callbackFlow {
            val listenerRegistration = meetingsQuery.addSnapshotListener { value, error ->
                error?.let {
                    cancel(message = "Error fetching meetings", cause = it)
                    return@addSnapshotListener
                }

                val meetings = ArrayList<Meeting>()
                value?.documentChanges?.forEach { change ->
                    change.document.toMeeting()?.let {
                        it.id = change.document.id
                        meetings.add(it)
                    }
                }

                trySend(meetings.ifEmpty { null }).isSuccess
            }
            awaitClose {
                listenerRegistration.remove()
            }
        }
    }
}