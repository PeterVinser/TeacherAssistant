package com.piotrokninski.teacherassistant.repository.calendar

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import com.piotrokninski.teacherassistant.model.Meeting
import com.piotrokninski.teacherassistant.util.WeekDate
import java.util.*

object CalendarProvider {
    private const val TAG = "CalendarProvider"

    fun insertMeeting(context: Context, meeting: Meeting): Long? {
        val contentResolver = context.contentResolver
        val timezone = Calendar.getInstance().timeZone.id

        val contentValues = ContentValues()

        contentValues.apply {
            put(CalendarContract.Events.TITLE, meeting.title)
            put(CalendarContract.Events.DESCRIPTION, meeting.description)
            meeting.date?.let {  put(CalendarContract.Events.DTSTART, it.time) }
            put(CalendarContract.Events.CALENDAR_ID, 1)
            put(CalendarContract.Events.EVENT_TIMEZONE, timezone)
        }

        if (meeting.singular) {
            val duration = (meeting.durationHours!! * 60 + meeting.durationMinutes!!) * 60 * 1000

            meeting.date?.let {
                contentValues.put(CalendarContract.Events.DTSTART, it.time + duration)
            }
        } else {
            meeting.weekDate?.let {
                val duration = "PT${it.durationHours}H${it.durationMinutes}M"
                val rrule = "FREQ=WEEKLY;BYDAY=${it.weekDay.rrule};INTERVAL=1"

                contentValues.put(CalendarContract.Events.DURATION, duration)
                contentValues.put(CalendarContract.Events.RRULE, rrule)
            }

            if (meeting.weekDate == null) {
                return null
            }
        }

        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)

        return uri?.lastPathSegment?.toLong()
    }

    fun updateMeeting(context: Context, meeting: Meeting) {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues()

        contentValues.apply {
            put(CalendarContract.Events.TITLE, meeting.title)
            put(CalendarContract.Events.DESCRIPTION, meeting.description)
            meeting.date?.let { put(CalendarContract.Events.DTSTART, it.time) }
        }

        if (meeting.singular) {
            val duration = (meeting.durationHours!! * 60 + meeting.durationMinutes!!) * 60 * 1000

            meeting.date?.let {
                contentValues.put(CalendarContract.Events.DTSTART, it.time + duration)
            }
        } else {
            meeting.weekDate?.let {
                val duration = "PT${it.durationHours}H${it.durationMinutes}M"
                val rrule = "FREQ=WEEKLY;BYDAY=${it.weekDay.rrule};INTERVAL=1"

                contentValues.put(CalendarContract.Events.DURATION, duration)
                contentValues.put(CalendarContract.Events.RRULE, rrule)
            }
        }

        meeting.calendarId?.let {
            val updateUri = ContentUris.withAppendedId(CalendarContract.EventDays.CONTENT_URI, it)
            contentResolver.update(updateUri, contentValues, null, null)
        }
    }

    fun deleteMeeting(context: Context, meeting: Meeting) {
        val contentResolver = context.contentResolver

        meeting.calendarId?.let {
            val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, it)
            contentResolver.delete(deleteUri, null, null)
        }
    }
}