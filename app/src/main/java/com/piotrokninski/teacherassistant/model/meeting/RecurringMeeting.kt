package com.piotrokninski.teacherassistant.model.meeting

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.piotrokninski.teacherassistant.model.meeting.RecurringMeeting.Companion.TABLE_NAME
import com.piotrokninski.teacherassistant.util.WeekDate
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = TABLE_NAME)
data class RecurringMeeting(
    val courseId: String? = null,
    val title: String,
    val description: String?,
    var date: Date,
    val attendeeIds: List<String>,
    val meetingDates: List<WeekDate>,
    val durationHours: Int,
    val durationMinutes: Int,
    @get:Exclude
    var id: String? = null,
    @get:Exclude
    @PrimaryKey(autoGenerate = true)
    var roomId: Int = 0
) {

    override fun equals(other: Any?): Boolean {
        return other?.let {
            if (it is RecurringMeeting) {
                this.courseId == it.courseId && this.title == it.title && this.description == it.description
                        && this.date == it.date && this.attendeeIds == it.attendeeIds
                        && this.meetingDates == it.meetingDates && this.durationHours == it.durationHours
                        && this.durationMinutes == it.durationMinutes
            } else {
                false
            }
        } ?: false
    }

    override fun hashCode(): Int {
        var result = courseId?.hashCode() ?: 0
        result = 31 * result + title.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + date.hashCode()
        result = 31 * result + attendeeIds.hashCode()
        result = 31 * result + meetingDates.hashCode()
        result = 31 * result + durationHours
        result = 31 * result + durationMinutes
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + roomId
        return result
    }

    companion object {
        fun DocumentSnapshot.toRecurringMeeting(): RecurringMeeting? {
            return try {
                val meetingDates = ArrayList<WeekDate>()

                (get(MEETING_DATES) as List<Map<*, *>>).forEach {
                    WeekDate.toWeekDate(it)?.let { weekDate -> meetingDates.add(weekDate) }
                }

                RecurringMeeting(
                    getString(COURSE_ID),
                    getString(TITLE)!!,
                    getString(DESCRIPTION),
                    getDate(DATE)!!,
                    get(ATTENDEE_IDS)!! as List<String>,
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

        const val TABLE_NAME = "recurring_meetings_table"

        private const val COURSE_ID = "courseId"
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