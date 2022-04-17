package com.piotrokninski.teacherassistant.model.meeting

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreRecurringMeetingsContract
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
                val courseId = getString(FirestoreRecurringMeetingsContract.COURSE_ID)
                val title = getString(FirestoreRecurringMeetingsContract.TITLE)!!
                val description = getString(FirestoreRecurringMeetingsContract.DESCRIPTION)
                val date = getDate(FirestoreRecurringMeetingsContract.DATE)!!
                val durationHours =
                    getLong(FirestoreRecurringMeetingsContract.DURATION_HOURS)!!.toInt()
                val durationMinutes =
                    getLong(FirestoreRecurringMeetingsContract.DURATION_MINUTES)!!.toInt()
                val attendeeIds =
                    get(FirestoreRecurringMeetingsContract.ATTENDEE_IDS)!! as ArrayList<String>
                val meetingDatesMap =
                    get(FirestoreRecurringMeetingsContract.MEETING_DATES) as ArrayList<Map<String, Any>>

                val meetingDates = ArrayList<WeekDate>()

                meetingDatesMap.forEach {
                    val weekDate = WeekDate.toWeekDate(it)
                    if (weekDate != null) meetingDates.add(weekDate)
                }

                RecurringMeeting(
                    courseId,
                    title,
                    description,
                    date,
                    attendeeIds,
                    meetingDates,
                    durationHours,
                    durationMinutes
                )

            } catch (e: Exception) {
                Log.e(TAG, "toRecurringMeeting: ")
                null
            }
        }

        private const val TAG = "RecurringMeeting"
    }
}