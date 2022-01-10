package com.piotrokninski.teacherassistant.repository.calendar

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import java.util.*

object CalendarProvider {

    fun insertEvent(context: Context) {
//        val contentResolver = context.contentResolver
//        val contentValues = ContentValues()

        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, "Event from android app")
            .putExtra(CalendarContract.Events.DESCRIPTION, "This is an event created by the TeacherAssistant")
            .putExtra(CalendarContract.Events.DTSTART, Calendar.getInstance().timeInMillis)
            .putExtra(CalendarContract.Events.DTEND, Calendar.getInstance().timeInMillis + 60*60*1000)
            .putExtra(CalendarContract.Events.CALENDAR_ID, 1)
            .putExtra(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().timeZone.id)

        context.startActivity(intent)
//        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)

    }
}