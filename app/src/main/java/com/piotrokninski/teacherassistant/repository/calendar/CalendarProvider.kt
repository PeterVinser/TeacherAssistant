package com.piotrokninski.teacherassistant.repository.calendar

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import com.piotrokninski.teacherassistant.model.meeting.RecurringMeeting
import com.piotrokninski.teacherassistant.util.WeekDate
import java.util.*

object CalendarProvider {
    private const val TAG = "CalendarProvider"

    fun insertMeeting(context: Context, meeting: Meeting): Long? {
        val contentResolver = context.contentResolver
        val timezone = Calendar.getInstance().timeZone.id

        val contentValues = ContentValues()

        val duration = (meeting.durationHours!! * 60 + meeting.durationMinutes!!) * 60 * 1000
        contentValues.apply {
            put(CalendarContract.Events.TITLE, meeting.title)
            put(CalendarContract.Events.DESCRIPTION, meeting.description)
            put(CalendarContract.Events.DTSTART, meeting.date.time)
            put(CalendarContract.Events.DTEND, meeting.date.time + duration)
            put(CalendarContract.Events.CALENDAR_ID, 1)
            put(CalendarContract.Events.EVENT_TIMEZONE, timezone)
        }

        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)

        Log.d(TAG, "insertMeeting: $meeting")

        return uri?.lastPathSegment?.toLong()
    }

    fun updateMeeting(context: Context, meeting: Meeting) {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues()

        val duration = (meeting.durationHours!! * 60 + meeting.durationMinutes!!) * 60 * 1000
        contentValues.apply {
            put(CalendarContract.Events.TITLE, meeting.title)
            put(CalendarContract.Events.DESCRIPTION, meeting.description)
            put(CalendarContract.Events.DTSTART, meeting.date.time)
            put(CalendarContract.Events.DTEND, meeting.date.time + duration)
        }

        meeting.calendarId?.let {
            val updateUri = ContentUris.withAppendedId(CalendarContract.EventDays.CONTENT_URI, it)
            contentResolver.update(updateUri, contentValues, null, null)
            Log.d(TAG, "updateMeeting: $meeting")
        }
    }

    fun deleteMeeting(context: Context, meeting: Meeting) {
        val contentResolver = context.contentResolver

        meeting.calendarId?.let {
            val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, it)
            contentResolver.delete(deleteUri, null, null)
            Log.d(TAG, "deleteMeeting: $meeting")
        }
    }

    private fun insertWeekDate(context: Context, title: String, description: String?, weekDate: WeekDate): Long? {
        val contentResolver = context.contentResolver
        val timezone = Calendar.getInstance().timeZone.id
        val contentValues = ContentValues()

        val date = getNextDate(weekDate)

        val duration = "PT${weekDate.durationHours}H${weekDate.durationMinutes}M"
        val rrule = "FREQ=WEEKLY;BYDAY=${weekDate.weekDay.rrule};INTERVAL=1"

        contentValues.apply {
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, description)
            put(CalendarContract.Events.DTSTART, date.time)
            put(CalendarContract.Events.DURATION, duration)
            put(CalendarContract.Events.RRULE, rrule)
            put(CalendarContract.Events.CALENDAR_ID, 1)
            put(CalendarContract.Events.EVENT_TIMEZONE, timezone)
        }

        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)

        Log.d(TAG, "insertWeekDate: $title, $weekDate")

        return uri?.lastPathSegment?.toLong()
    }

    fun insertRecurringMeeting(context: Context, recurringMeeting: RecurringMeeting) {
        recurringMeeting.meetingDates.forEach { weekDate ->
            weekDate.calendarId = insertWeekDate(context, recurringMeeting.title, recurringMeeting.description, weekDate)
        }
    }

    private fun deleteWeekDate(context: Context, weekDate: WeekDate) {
        val contentResolver = context.contentResolver

        Log.d(TAG, "deleteWeekDate: ${weekDate.calendarId}")

        weekDate.calendarId?.let {
            val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, it)
            contentResolver.delete(deleteUri, null, null)
            Log.d(TAG, "deleteWeekDate: $weekDate")
        }
    }

    fun deleteRecurringMeeting(context: Context, meeting: RecurringMeeting) {
        meeting.meetingDates.forEach { weekDate ->
            deleteWeekDate(context, weekDate)
        }
    }

    private fun getNextDate(weekDate: WeekDate): Date {
        val calendar = Calendar.getInstance()
        val currentDay =
            if (calendar.get(Calendar.DAY_OF_WEEK) == 1) 7 else calendar.get(Calendar.DAY_OF_WEEK) - 1
        val weekDayNum = weekDate.weekDay.id

        var daysToAdd = weekDayNum - currentDay
        if (daysToAdd <= 0) daysToAdd += 7

        calendar.add(Calendar.DAY_OF_WEEK, daysToAdd)
        calendar.set(Calendar.HOUR_OF_DAY, weekDate.hour)
        calendar.set(Calendar.MINUTE, weekDate.minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }
}