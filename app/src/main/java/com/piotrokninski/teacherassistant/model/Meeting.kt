package com.piotrokninski.teacherassistant.model

import android.util.Log
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.piotrokninski.teacherassistant.util.WeekDate
import java.util.*
import kotlin.collections.ArrayList

data class Meeting(
    var id: String? = null,
    val courseId: String? = null,
    val lessonId: String? = null,
    var attendeeIds: List<String>? = null,
    var title: String? = null,
    var description: String? = null,
    var date: Date? = null,
    var durationHours: Int? = null,
    var durationMinutes: Int? = null,
    var singular: Boolean,
    val completed: Boolean,
    var weekDates: ArrayList<WeekDate>? = null,
    @get:Exclude
    @PrimaryKey(autoGenerate = true)
    var roomId: Int = 0,
    @get:Exclude
    var calendarId: Long? = null
) {

    @get:Exclude
    val isComplete: Boolean
        get() = (!title.isNullOrEmpty() && !description.isNullOrEmpty()
                && durationHours != null && durationMinutes != null
                && date != null && (singular == (weekDates == null)))

    fun dateToString(): String? {
        return if (singular) {
            if (date == null || durationHours == null || durationMinutes == null) {
                ""
            } else {
                "${date.toString()}, ${durationHours}h, ${durationMinutes}min"
            }
        } else {
            weekDates?.get(0)?.toString()
        }
    }

    companion object {
        fun DocumentSnapshot.toMeeting(): Meeting? =
            toMeeting(data!!)

        fun toMeeting(map: Map<String, Any>): Meeting? {
            return try {
                val weekDates = ArrayList<WeekDate>()

                (map[Contract.WEEK_DATES] as List<Map<*, *>>?)?.forEach {
                    WeekDate.toWeekDate(it)?.let { weekDate -> weekDates.add(weekDate) }
                }

                Meeting(
                    map[Contract.ID] as String?,
                    map[Contract.COURSE_ID] as String?,
                    map[Contract.LESSON_ID] as String?,
                    map[Contract.ATTENDEE_IDS] as ArrayList<String>,
                    map[Contract.TITLE] as String,
                    map[Contract.DESCRIPTION] as String?,
                    (map[Contract.DATE] as Timestamp).toDate(),
                    (map[Contract.DURATION_HOURS] as Long).toInt(),
                    (map[Contract.DURATION_MINUTES] as Long).toInt(),
                    map[Contract.SINGULAR] as Boolean,
                    map[Contract.COMPLETED] as Boolean,
                    weekDates
                )
            } catch (e: Exception) {
                Log.e(TAG, "toMeeting: ", e)
                null
            }
        }
        private const val TAG = "Meeting"
    }

    object Contract {

        const val COLLECTION_NAME = "meetings"

        const val TABLE_NAME = "meetings_table"

        const val ID = "id"
        const val COURSE_ID = "courseId"
        const val LESSON_ID = "lessonId"
        const val ATTENDEE_IDS = "attendeeIds"
        const val TITLE = "title"
        const val DATE = "date"
        const val SINGULAR = "singular"
        const val COMPLETED = "completed"
        const val DESCRIPTION = "description"
        const val DURATION_HOURS = "durationHours"
        const val DURATION_MINUTES = "durationMinutes"
        const val WEEK_DATES = "weekDates"

    }
}
