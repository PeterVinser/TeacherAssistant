package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreRecurringMeetingsContract
import com.piotrokninski.teacherassistant.model.meeting.RecurringMeeting
import com.piotrokninski.teacherassistant.model.meeting.RecurringMeeting.Companion.toRecurringMeeting
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object FirestoreRecurringMeetingsRepository {
    private const val TAG = "FirestoreRecurringMeeti"

    suspend fun getRecurringMeetings(userId: String): ArrayList<RecurringMeeting>? {
        val db = FirebaseFirestore.getInstance()

        val recurringMeetingsRef = db.collection(FirestoreRecurringMeetingsContract.COLLECTION_NAME)

        val query = recurringMeetingsRef.whereArrayContains(
            FirestoreRecurringMeetingsContract.ATTENDEE_IDS,
            userId
        ).orderBy(FirestoreRecurringMeetingsContract.DATE)

        return try {
            val recurringMeetings = ArrayList<RecurringMeeting>()

            query.get().await().forEach { recurringMeeting ->
                recurringMeeting.toRecurringMeeting()?.let { recurringMeetings.add(it) }
            }

            if (recurringMeetings.isEmpty()) {
                null
            } else {
                recurringMeetings
            }
        } catch (e: Exception) {
            Log.e(TAG, "getRecurringMeetings: ", e)
            null
        }
    }
}