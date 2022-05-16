package com.piotrokninski.teacherassistant.model.meeting

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.piotrokninski.teacherassistant.model.meeting.Meeting.Companion.TABLE_NAME
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = TABLE_NAME)
data class Meeting(
    val courseId: String? = null,
    val lessonId: String? = null,
    val attendeeIds: List<String>,
    val title: String,
    val date: Date,
    val singular: Boolean,
    val completed: Boolean,
    val description: String?,
    val durationHours: Int?,
    val durationMinutes: Int?,
    @get:Exclude
    var id: String? = null,
    @get:Exclude
    @PrimaryKey(autoGenerate = true)
    var roomId: Int = 0,
    @get:Exclude
    var calendarId: Long? = null
) {

    override fun equals(other: Any?): Boolean {
        return other?.let {
            if (it is Meeting) {
                this.courseId == it.courseId && this.lessonId == it.lessonId && this.attendeeIds == it.attendeeIds &&
                        this.title == it.title && this.date == it.date && this.completed == it.completed && this.description == it.description &&
                        this.durationHours == it.durationHours && this.durationMinutes == it.durationMinutes && this.id == it.id
            } else {
                false
            }
        } ?: false
    }

    override fun hashCode(): Int {
        var result = courseId?.hashCode() ?: 0
        result = 31 * result + (lessonId?.hashCode() ?: 0)
        result = 31 * result + attendeeIds.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + completed.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (durationHours ?: 0)
        result = 31 * result + (durationMinutes ?: 0)
        result = 31 * result + roomId
        result = 31 * result + (id?.hashCode() ?: 0)
        result = (31 * result + (calendarId ?: 0)).toInt()
        return result
    }

    companion object {
        fun DocumentSnapshot.toSingularMeeting(): Meeting? {
            return try {
                return Meeting(
                    getString(COURSE_ID),
                    getString(LESSON_ID),
                    get(ATTENDEE_IDS) as ArrayList<String>,
                    getString(TITLE)!!,
                    getDate(DATE)!!,
                    getBoolean(SINGULAR)!!,
                    getBoolean(COMPLETED)!!,
                    getString(DESCRIPTION),
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

        const val TABLE_NAME = "meetings_table"

        private const val COURSE_ID = "courseId"
        private const val LESSON_ID = "lessonId"
        const val ATTENDEE_IDS = "attendeeIds"
        private const val TITLE = "title"
        const val DATE = "date"
        const val SINGULAR = "singular"
        const val COMPLETED = "completed"
        private const val DESCRIPTION = "description"
        private const val DURATION_HOURS = "durationHours"
        private const val DURATION_MINUTES = "durationMinutes"

        private const val TAG = "Meeting"
    }
}
