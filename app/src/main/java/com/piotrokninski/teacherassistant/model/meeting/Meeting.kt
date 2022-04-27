package com.piotrokninski.teacherassistant.model.meeting

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
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
                return Meeting(
                    getString(COURSE_ID),
                    getString(LESSON_ID),
                    get(ATTENDEE_IDS) as ArrayList<String>,
                    getString(TITLE)!!,
                    getDate(DATE)!!,
                    getBoolean(COMPLETED)!!,
                    getString(DESCRIPTION)!!,
                    getLong(DURATION_HOURS)!!.toInt(),
                    getLong(DURATION_MINUTES)!!.toInt()
                )
            } catch (e: Exception) {
                Log.e(TAG, "toMeeting: ", e)
                null
            }
        }

        //Contract
        const val COLLECTION_NAME = "meetings"

        const val COURSE_ID = "courseId"
        private const val LESSON_ID = "lessonId"
        const val ATTENDEE_IDS = "attendeeIds"
        private const val TITLE = "title"
        const val DATE = "date"
        private const val COMPLETED = "completed"
        private const val DESCRIPTION = "description"
        private const val DURATION_HOURS = "durationHours"
        private const val DURATION_MINUTES = "durationMinutes"

        private const val TAG = "Meeting"
    }
}
