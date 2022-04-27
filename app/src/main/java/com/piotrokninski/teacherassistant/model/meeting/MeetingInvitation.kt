package com.piotrokninski.teacherassistant.model.meeting

import android.util.Log
import androidx.databinding.BaseObservable
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.piotrokninski.teacherassistant.util.WeekDate
import java.lang.Exception
import java.util.*

data class MeetingInvitation(
    var title: String? = null,
    var description: String? = null,
    val invitingUserId: String,
    val invitingUserFullName: String,
    var invitedUserId: String? = null,
    var invitedUserFullName: String? = null,
    var durationHours: Int? = null,
    var durationMinutes: Int? = null,
    var date: Date? = null,
    var weekDate: WeekDate? = null,
    var mode: String = MEETING_TYPE_SINGULAR,
    val status: String = STATUS_PENDING
) : BaseObservable() {

    @get:Exclude
    val isComplete: Boolean
        get() = when (mode) {
            MEETING_TYPE_SINGULAR ->
                !title.isNullOrEmpty() && !description.isNullOrEmpty() && invitedUserId != null && durationHours != null && durationMinutes != null && date != null

            MEETING_TYPE_RECURRING ->
                !title.isNullOrEmpty() && !description.isNullOrEmpty() && invitedUserId != null && weekDate != null

            else -> false
        }

    @get:Exclude
    var id: String? = null

    fun dateToString(): String? {
        //TODO replace the temporary implementation with multiple dates/week dates.
        return when (mode) {
            MEETING_TYPE_SINGULAR ->
                if (date == null) null else "${date?.toLocaleString()}, ${durationHours}h ${durationMinutes}min"

            MEETING_TYPE_RECURRING ->
                if (weekDate == null) null else "${weekDate?.toString()}, ${weekDate?.durationHours}h ${weekDate?.durationMinutes}min"

            else -> null
        }
    }

    companion object {

        fun DocumentSnapshot.toMeetingInvitation(): MeetingInvitation? {
            return try {
                MeetingInvitation(
                    getString(TITLE)!!,
                    getString(DESCRIPTION)!!,
                    getString(INVITING_USER_ID)!!,
                    getString(INVITING_USER_FULL_NAME)!!,
                    getString(INVITED_USER_ID)!!,
                    getString(INVITED_USER_FULL_NAME)!!,
                    getLong(DURATION_HOURS)?.toInt(),
                    getLong(DURATION_MINUTES)?.toInt(),
                    getDate(DATE),
                    get(WEEK_DATE)?.let { WeekDate.toWeekDate(it as Map<String, Any>) },
                    getString(MODE)!!,
                    getString(STATUS)!!
                )
            } catch (e: Exception) {
                Log.e(TAG, "toMeetingInvitation: ", e)
                null
            }
        }

        //Database Contract
        const val COLLECTION_NAME = "meetingInvitations"

        private const val TITLE = "title"
        private const val DESCRIPTION = "description"
        private const val INVITING_USER_ID = "invitingUserId"
        private const val INVITING_USER_FULL_NAME = "invitingUserFullName"
        private const val INVITED_USER_ID = "invitedUserId"
        private const val INVITED_USER_FULL_NAME = "invitedUserFullName"
        private const val DURATION_HOURS = "durationHours"
        private const val DURATION_MINUTES = "durationMinutes"
        private const val DATE = "date"
        private const val WEEK_DATE = "weekDate"
        private const val MODE = "mode"
        private const val STATUS = "status"

        const val MEETING_TYPE_SINGULAR = "meetingTypeSingular"
        const val MEETING_TYPE_RECURRING = "meetingTypeRecurring"

        const val STATUS_PENDING = "pending"
        const val STATUS_APPROVED = "approved"

        private const val TAG = "MeetingHolder"
    }
}
