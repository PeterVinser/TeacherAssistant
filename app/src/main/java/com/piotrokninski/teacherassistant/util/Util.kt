package com.piotrokninski.teacherassistant.util

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val TAG = "Util"
object Util {

    fun formatTime(hour: Int?, minute: Int?): String? {
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
            "$weekDay, $formattedTime"
        } else if (weekDay == null && formattedTime != null) {
            formattedTime
        } else if (weekDay != null && formattedTime == null) {
            weekDay
        } else {
            null
        }
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