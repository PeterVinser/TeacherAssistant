package com.piotrokninski.teacherassistant.util

import android.util.Log
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

data class WeekDate(
    var weekDay: String? = null,
    var hour: Int? = null,
    var minute: Int? = null,
    var durationHours: Int = 1,
    var durationMinutes: Int = 0,
    var offset: String = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()).toString()
) {

    fun isComplete(): Boolean {
        return weekDay != null && hour != null && minute != null
    }

    fun updateWeekDay(weekDay: WeekDays) {
        this.weekDay = weekDay.toString()
    }

    private fun timeToString(): String? {
        if (hour == null || minute == null) {
            return null
        }

        val hourString = if (hour!! < 10) "0$hour" else hour.toString()
        val minuteString = if (minute!! < 10) "0$minute" else minute.toString()

        return "$hourString:$minuteString"
    }

    fun toMap(): Map<String, Any>? {
        return if (isComplete()) {
            mapOf (
                "weekDay" to weekDay!!.toString(),
                "hour" to hour!!,
                "minute" to minute!!,
                "offset" to ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()),
                "durationsHours" to durationHours,
                "durationMinutes" to durationMinutes
            )
        } else {
            null
        }
    }

    fun toStringWithOffset(): String? {
        if (toString().isEmpty()) {
            return null
        }

        val offset = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())

        return "${toString()} $offset"
    }

    override fun toString(): String {
        if (timeToString() == null && weekDay == null) {
            return ""
        } else if (timeToString() == null) {
            return weekDay!!
        } else if (weekDay == null) {
            return timeToString()!!
        }

        return "${weekDay!!} ${timeToString()}"
    }

    companion object {
        private const val TAG = "WeekDate"

        private const val WEEK_DAY = "weekDay"
        private const val HOUR = "hour"
        private const val MINUTE = "minute"
        private const val DURATION_HOURS = "durationHours"
        private const val DURATION_MINUTES = "durationMinutes"

        fun toWeekDate(map: Map<String, Any>) : WeekDate? {
            return try {
                val weekDay = map[WEEK_DAY] as String
                val hour = (map[HOUR] as Long).toInt()
                val minute = (map[MINUTE] as Long).toInt()
                val durationHours = (map[DURATION_HOURS] as Long).toInt()
                val durationMinutes = (map[DURATION_MINUTES] as Long).toInt()
                val offset = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()).toString()

                WeekDate(weekDay, hour, minute, durationHours, durationMinutes, offset)
            } catch (e: Exception) {
                Log.e(TAG, "toWeekDate: ", e)
                null
            }
        }

        val WEEK_DAYS_NUMERICAL = mapOf(
            "Monday" to 1,
            "Tuesday" to 2,
            "Wednesday" to 3,
            "Thursday" to 4,
            "Friday" to 5,
            "Saturday" to 6,
            "Sunday" to 7
        )
    }
}