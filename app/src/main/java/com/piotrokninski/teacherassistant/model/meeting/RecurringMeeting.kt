package com.piotrokninski.teacherassistant.model.meeting

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.util.WeekDate
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

data class RecurringMeeting(
    val courseId: String? = null,
    val title: String,
    val description: String?,
    var date: Date,
    val attendeeIds: ArrayList<String>,
    val meetingDates: ArrayList<WeekDate>,
    val durationHours: Int,
    val durationMinutes: Int
) {
    companion object {
        fun DocumentSnapshot.toRecurringMeeting(): RecurringMeeting? {
            return try {
                val meetingDates = ArrayList<WeekDate>()

                (get(MEETING_DATES) as ArrayList<Map<String, Any>>).forEach {
                    WeekDate.toWeekDate(it)?.let { weekDate -> meetingDates.add(weekDate) }
                }

                RecurringMeeting(
                    getString(COURSE_ID),
                    getString(TITLE)!!,
                    getString(DESCRIPTION),
                    getDate(DATE)!!,
                    get(ATTENDEE_IDS)!! as ArrayList<String>,
                    meetingDates,
                    getLong(DURATION_HOURS)!!.toInt(),
                    getLong(DURATION_MINUTES)!!.toInt()
                )

            } catch (e: Exception) {
                Log.e(TAG, "toRecurringMeeting: ")
                null
            }
        }

        //Contract
        const val COLLECTION_NAME = "recurringMeetings"

        const val COURSE_ID = "courseId"
        private const val TITLE = "title"
        private const val DESCRIPTION = "description"
        const val DATE = "date"
        private const val DURATION_HOURS = "durationHours"
        private const val DURATION_MINUTES = "durationMinutes"
        const val ATTENDEE_IDS = "attendeeIds"
        private const val MEETING_DATES = "meetingDates"

        private const val TAG = "RecurringMeeting"
    }
}