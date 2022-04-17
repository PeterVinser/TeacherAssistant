package com.piotrokninski.teacherassistant.model.meeting

import androidx.databinding.BaseObservable
import com.piotrokninski.teacherassistant.util.WeekDate
import java.util.*

data class MeetingInvitation(
    var title: String? = null,
    var description: String? = null,
    var upcomingDate: Date? = null,
    val attendeeIds: ArrayList<String>,
    var durationHours: Int? = null,
    var durationMinutes: Int? = null,
    var date: Date? = null,
    var weekDate: WeekDate? = null,
    var mode: String = MEETING_TYPE_SINGULAR
) : BaseObservable() {

//    var mode by Delegates.observable(MEETING_TYPE_SINGULAR) { _, _, _ ->
//        notifyPropertyChanged()
//    }

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

    fun toSingularMeeting(): Meeting? {
        return if (!title.isNullOrEmpty() && !description.isNullOrEmpty() && attendeeIds.isNotEmpty() && durationHours != null && durationMinutes != null && date != null) {
            Meeting(
                attendeeIds = attendeeIds,
                title = title!!,
                date = date!!,
                completed = date!! < Date(),
                description = description!!,
                durationHours = durationHours,
                durationMinutes = durationMinutes
            )
        } else {
            null
        }
    }

    fun toRecurringMeeting(): RecurringMeeting? {
        return if (!title.isNullOrEmpty() && !description.isNullOrEmpty() && attendeeIds.isNotEmpty() && weekDate != null) {
            RecurringMeeting(
                title = title!!,
                description = description!!,
                date = getUpcomingMeetingDate(weekDate!!),
                attendeeIds = attendeeIds,
                meetingDates = arrayListOf(weekDate!!),
                durationHours = weekDate!!.durationHours,
                durationMinutes = weekDate!!.durationMinutes
            )
        } else {
            null
        }
    }

    private fun getUpcomingMeetingDate(weekDate: WeekDate): Date {
        val calendar = Calendar.getInstance()

        //For some reason the day of week starts with sunday (sunday->1)????
        val weekDayNumber = if (weekDate.weekDay!!.id == 7) 0 else weekDate.weekDay!!.id + 1

        calendar.set(Calendar.DAY_OF_WEEK, weekDayNumber)
        calendar.set(Calendar.HOUR_OF_DAY, weekDate.hour!!)
        calendar.set(Calendar.MINUTE, weekDate.minute!!)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.time < Date()) {
            calendar.add(Calendar.DAY_OF_WEEK, 7)
        }

        return calendar.time
    }

    companion object {
        const val MEETING_TYPE_SINGULAR = "meetingTypeSingular"
        const val MEETING_TYPE_RECURRING = "meetingTypeRecurring"

        private const val TAG = "MeetingHolder"
    }
}
