package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import com.piotrokninski.teacherassistant.model.meeting.Meeting.Companion.toSingularMeeting
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

object FirestoreMeetingRepository {
    private const val TAG = "FirestoreMeetingReposit"

    fun addMeeting(meeting: Meeting) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Meeting.COLLECTION_NAME).add(meeting)
    }

    suspend fun getMeetings(userId: String): ArrayList<Meeting>? {
        val db = FirebaseFirestore.getInstance()

        val meetingsRef = db.collection(Meeting.COLLECTION_NAME)

        val query = meetingsRef
            .whereArrayContains(Meeting.ATTENDEE_IDS, userId)
            .orderBy(Meeting.DATE, Query.Direction.ASCENDING)

        return try {
            val meetings = ArrayList<Meeting>()

            query.get().await().forEach { meeting ->
                meeting?.toSingularMeeting()?.let { meetings.add(it) }
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

        val meetingsRef = db.collection(Meeting.COLLECTION_NAME)

        val query = meetingsRef.whereArrayContains(Meeting.ATTENDEE_IDS, userId)
            .whereGreaterThanOrEqualTo(Meeting.DATE, startDate)
            .whereLessThanOrEqualTo(Meeting.DATE, endDate)
            .orderBy(Meeting.DATE, Query.Direction.ASCENDING)

        return try {
            val meetings = ArrayList<Meeting>()

            query.get().await().forEach { meeting ->
                meeting?.toSingularMeeting()?.let {
                    meetings.add(it)
                }
            }

            if (meetings.isEmpty()) {
                null
            } else {
                meetings
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUpcomingWeekMeetings: ", e)
             null
        }
    }

    suspend fun getUpcomingSingularMeetings(userId: String): ArrayList<Meeting>? {
        val db = FirebaseFirestore.getInstance()

        val meetingsQuery = db.collection(Meeting.COLLECTION_NAME)
            .whereArrayContains(Meeting.ATTENDEE_IDS, userId)
            .whereEqualTo(Meeting.SINGULAR, true)
            .whereEqualTo(Meeting.COMPLETED, false)

        return try {
            val meetings = ArrayList<Meeting>()

            meetingsQuery.get().await().forEach { meeting ->
                meeting?.toSingularMeeting()?.let {
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

        val meetingsQuery = db.collection(Meeting.COLLECTION_NAME)
            .whereArrayContains(Meeting.ATTENDEE_IDS, userId)
            .whereEqualTo(Meeting.SINGULAR, true)
            .whereEqualTo(Meeting.COMPLETED, false)

        return callbackFlow {
            val listenerRegistration = meetingsQuery.addSnapshotListener { value, error ->
                error?.let {
                    cancel(message = "Error fetching meetings", cause = it)
                    return@addSnapshotListener
                }

                val meetings = ArrayList<Meeting>()
                value?.documentChanges?.forEach { change ->
                    change.document.toSingularMeeting()?.let {
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