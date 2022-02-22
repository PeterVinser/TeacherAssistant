package com.piotrokninski.teacherassistant.util

import java.time.LocalDateTime
import java.time.ZoneId

data class WeekDate(
    var weekDay: WeekDays? = null,
    var hour: Int? = null,
    var minute: Int? = null
) {

    fun isComplete(): Boolean {
        return weekDay != null && hour != null && minute != null
    }

    private fun timeToString(): String? {
        if (hour == null || minute == null) {
            return null
        }

        val hourString = if (hour!! < 10) "0$hour" else hour.toString()
        val minuteString = if (minute!! < 10) "0$minute" else minute.toString()

        return "$hourString:$minuteString"
    }

    private fun weekDayToString(): String? {
        if (weekDay == null) {
            return null
        }

        return weekDay.toString()
    }

    fun toStringWithOffset(): String? {
        if (toString().isEmpty()) {
            return null
        }

        val offset = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now())

        return "${toString()} $offset"
    }

    override fun toString(): String {
        if (timeToString() == null && weekDayToString() == null) {
            return ""
        } else if (timeToString() == null) {
            return weekDayToString()!!
        } else if (weekDayToString() == null) {
            return timeToString()!!
        }

        return "${weekDayToString()!!} ${timeToString()}"
    }
}