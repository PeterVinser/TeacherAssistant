package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreMeetingContract
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import com.piotrokninski.teacherassistant.model.meeting.Meeting.Companion.toMeeting
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.lang.IllegalArgumentException

object FirestoreMeetingRepository {
    private const val TAG = "FirestoreMeetingReposit"

    suspend fun getMeetings(userId: String, viewType: String): ArrayList<Meeting>? {
        val db = FirebaseFirestore.getInstance()

        val meetingsRef = db.collection(FirestoreMeetingContract.COLLECTION_NAME)

        val userType = when (viewType) {
            AppConstants.VIEW_TYPE_STUDENT -> FirestoreMeetingContract.STUDENT_ID

            AppConstants.VIEW_TYPE_TUTOR -> FirestoreMeetingContract.TUTOR_FULL_NAME

            else -> throw IllegalArgumentException("Unknown viewType")
        }

        val query = meetingsRef
            .whereNotEqualTo(userType, userId)

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
}