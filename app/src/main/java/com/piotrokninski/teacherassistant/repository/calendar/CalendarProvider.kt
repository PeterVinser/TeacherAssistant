package com.piotrokninski.teacherassistant.repository.calendar

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import java.util.*
import java.util.Calendar.MONDAY

object CalendarProvider {

    fun insertEvent(context: Context) {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues()

        val calendar = Calendar.Builder().setWeekDate(2022, 8, MONDAY).build()

        contentValues.apply {
            put(CalendarContract.Events.DTSTART, calendar.timeInMillis)
            put(CalendarContract.Events.DTEND, calendar.timeInMillis + 60*60*1000)
            put(CalendarContract.Events.TITLE, "Event from android app")
            put(CalendarContract.Events.CALENDAR_ID, 1)
            put(CalendarContract.Events.EVENT_TIMEZONE, calendar.timeZone.id)
        }

        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, "Event from android app")
            .putExtra(CalendarContract.Events.DESCRIPTION, "This is an event created by the TeacherAssistant")
            .putExtra(CalendarContract.Events.DTSTART, calendar.timeInMillis)
            .putExtra(CalendarContract.Events.DTEND, calendar.timeInMillis + 60*60*1000)
            .putExtra(CalendarContract.Events.CALENDAR_ID, 1)
            .putExtra(CalendarContract.Events.EVENT_TIMEZONE, calendar.timeZone.id)

//        context.startActivity(intent)
        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)
    }

    fun insertMeetingDates(context: Context, meeting: Meeting) {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues()
    }
}