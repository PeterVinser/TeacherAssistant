package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import com.piotrokninski.teacherassistant.model.meeting.Meeting.Companion.toMeeting
import kotlinx.coroutines.tasks.await
import java.util.*

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

        val meetingsRef = db.collection(Meeting.COLLECTION_NAME)

        val query = meetingsRef.whereArrayContains(Meeting.ATTENDEE_IDS, userId)
            .whereGreaterThanOrEqualTo(Meeting.DATE, startDate)
            .whereLessThanOrEqualTo(Meeting.DATE, endDate)
            .orderBy(Meeting.DATE, Query.Direction.ASCENDING)

        return try {
            val meetings = ArrayList<Meeting>()

            query.get().await().forEach { meeting ->
                meeting?.toMeeting()?.let { 
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
}