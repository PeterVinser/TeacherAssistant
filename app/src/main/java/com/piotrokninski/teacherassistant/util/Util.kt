package com.piotrokninski.teacherassistant.util

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

private const val TAG = "Util"

object Util {

    private fun formatTime(hour: Int?, minute: Int?): String? {
        return if (hour != null && minute != null) {
            val hourString = if (hour < 10) "0$hour" else hour.toString()
            val minuteString = if (minute < 10) "0$minute" else minute.toString()

            "$hourString:$minuteString"
        } else {
            null
        }
    }

    fun formatMeetingTime(weekDay: String?, hour: Int?, minute: Int?): String? {

        val formattedTime = formatTime(hour, minute)

        Log.d(TAG, "formatMeetingTime: $formattedTime")

        return if (weekDay != null && formattedTime != null) {
            "$weekDay $formattedTime"
        } else if (weekDay == null && formattedTime != null) {
            formattedTime
        } else if (weekDay != null && formattedTime == null) {
            weekDay
        } else {
            null
        }
    }

    fun getMeetingDateWithOffset(weekDay: String, hour: Int, minute: Int): String {
        val offset = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())

        val time = formatTime(hour, minute)!!

        return "$weekDay $time $offset"
    }

    fun getLocalEndOfDay(date: Date): Date {
        return getLocalDateWithTime(date, 23, 59, 59)
    }

    fun getLocalDateWithTime(date: Date, hour: Int, minute: Int, second: Int): Date {
        return Date(
            date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(
                LocalTime.MIN
            ).toInstant(ZoneOffset.UTC).toEpochMilli() - ZoneId.systemDefault().rules.getOffset(
                LocalDateTime.now()
            ).totalSeconds * 1000 + (hour * 60 * 60 + minute * 60 + second) * 1000
        )
    }

    fun <T> T.serializeToMap(): Map<String, Any> {
        return convert()
    }

    private inline fun <I, reified O> I.convert(): O {
        val gson = Gson()
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<O>() {}.type)
    }
}