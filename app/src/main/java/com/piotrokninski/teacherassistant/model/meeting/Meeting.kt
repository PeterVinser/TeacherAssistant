package com.piotrokninski.teacherassistant.model.meeting

import android.provider.CalendarContract
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreMeetingContract
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

data class Meeting(
    val courseId: String? = null,
    val lessonId: String? = null,
    val attendeeIds: ArrayList<String>,
    val title: String,
    val date: Date,
    val completed: Boolean,
    val description: String,
    val durationHours: Int?,
    val durationMinutes: Int?
) {
    companion object {
        fun DocumentSnapshot.toMeeting(): Meeting? {
            return try {
                val courseId = getString(FirestoreMeetingContract.COURSE_ID)
                val lessonId = getString(FirestoreMeetingContract.LESSON_ID)
                val attendeeIds = get(FirestoreMeetingContract.ATTENDEE_IDS)!! as ArrayList<String>
                val title = getString(FirestoreMeetingContract.TITLE)!!
                val date = getDate(FirestoreMeetingContract.DATE)!!
                val completed = getBoolean(FirestoreMeetingContract.COMPLETED)!!
                val description = getString(FirestoreMeetingContract.DESCRIPTION)!!
                val durationHours = getLong(FirestoreMeetingContract.DURATION_HOURS)!!.toInt()
                val durationMinutes = getLong(FirestoreMeetingContract.DURATION_MINUTES)!!.toInt()

                return Meeting(
                    courseId,
                    lessonId,
                    attendeeIds,
                    title,
                    date,
                    completed,
                    description,
                    durationHours,
                    durationMinutes
                )
            } catch (e: Exception) {
                Log.e(TAG, "toMeeting: ", e)
                null
            }
        }

        private const val TAG = "Meeting"
    }
}
