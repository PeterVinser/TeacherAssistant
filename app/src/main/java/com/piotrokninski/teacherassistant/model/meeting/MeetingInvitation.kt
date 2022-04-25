package com.piotrokninski.teacherassistant.model.meeting

import androidx.databinding.BaseObservable
import com.google.firebase.firestore.Exclude
import com.piotrokninski.teacherassistant.util.WeekDate
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

//    var mode by Delegates.observable(MEETING_TYPE_SINGULAR) { _, _, _ ->
//        notifyPropertyChanged()
//    }

    @get:Exclude
    val isComplete: Boolean
        get() = when (mode) {
            MEETING_TYPE_SINGULAR ->
                !title.isNullOrEmpty() && !description.isNullOrEmpty() && invitedUserId != null && durationHours != null && durationMinutes != null && date != null

            MEETING_TYPE_RECURRING ->
                !title.isNullOrEmpty() && !description.isNullOrEmpty() && invitedUserId != null && weekDate != null

            else -> false
        }

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
        const val MEETING_TYPE_SINGULAR = "meetingTypeSingular"
        const val MEETING_TYPE_RECURRING = "meetingTypeRecurring"

        const val STATUS_PENDING = "pending"
        const val STATUS_APPROVED = "approved"

        private const val TAG = "MeetingHolder"
    }
}
