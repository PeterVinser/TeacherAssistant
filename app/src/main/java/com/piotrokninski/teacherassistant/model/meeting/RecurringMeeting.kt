package com.piotrokninski.teacherassistant.model.meeting

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreRecurringMeetingsContract
import com.piotrokninski.teacherassistant.util.WeekDate
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class RecurringMeeting(
    val courseId: String,
    var date: Date,
    val durationHours: Int,
    val durationMinutes: Int,
    val attendeeIds: ArrayList<String>,
    val meetingDates: ArrayList<WeekDate>
) {
    companion object {
        fun DocumentSnapshot.toRecurringMeeting(): RecurringMeeting? {
            return try {
                val courseId = getString(FirestoreRecurringMeetingsContract.COURSE_ID)!!
                val date = getDate(FirestoreRecurringMeetingsContract.DATE)!!
                val durationHours =
                    getLong(FirestoreRecurringMeetingsContract.DURATION_HOURS)!!.toInt()
                val durationMinutes =
                    getLong(FirestoreRecurringMeetingsContract.DURATION_MINUTES)!!.toInt()
                val attendeeIds = get(FirestoreRecurringMeetingsContract.ATTENDEE_IDS)!! as ArrayList<String>
                val meetingDatesMap =
                    get(FirestoreRecurringMeetingsContract.MEETING_DATES) as ArrayList<Map<String, Any>>

                val meetingDates = ArrayList<WeekDate>()

                meetingDatesMap.forEach {
                    val weekDate = WeekDate.toWeekDate(it)
                    if (weekDate != null) meetingDates.add(weekDate)
                }

                RecurringMeeting(courseId, date, durationHours, durationMinutes, attendeeIds, meetingDates)

            } catch (e: Exception) {
                Log.e(TAG, "toRecurringMeeting: ")
                null
            }
        }

        private const val TAG = "RecurringMeeting"
    }
}