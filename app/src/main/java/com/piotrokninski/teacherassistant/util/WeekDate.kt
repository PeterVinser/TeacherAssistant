package com.piotrokninski.teacherassistant.util

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.Exclude
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

data class WeekDate(
    var weekDay: WeekDays,
    var hour: Int,
    var minute: Int,
    var durationHours: Int = 1,
    var durationMinutes: Int = 0,
    val timeZone: String = TimeZone.getDefault().id
) {

    fun updateWeekDay(weekDay: WeekDays) {
        this.weekDay = weekDay
    }

    private fun timeToString(): String {
        val hourString = if (hour < 10) "0$hour" else hour.toString()
        val minuteString = if (minute < 10) "0$minute" else minute.toString()

        return "$hourString:$minuteString"
    }

    override fun toString(): String {
        return "$weekDay ${timeToString()}"
    }

    fun toLocalString(context: Context): String {
        val localWeekDay = weekDay.stringId.let { context.getString(it) }

        return "$localWeekDay ${timeToString()}"
    }

    companion object {
        private const val TAG = "WeekDate"

        private const val WEEK_DAY = "weekDay"
        private const val HOUR = "hour"
        private const val MINUTE = "minute"
        private const val DURATION_HOURS = "durationHours"
        private const val DURATION_MINUTES = "durationMinutes"

        fun toWeekDate(map: Map<String, Any>): WeekDate? {
            return try {
                val weekDay = WeekDays.valueOf(map[WEEK_DAY] as String)
                val hour = (map[HOUR] as Long).toInt()
                val minute = (map[MINUTE] as Long).toInt()
                val durationHours = (map[DURATION_HOURS] as Long).toInt()
                val durationMinutes = (map[DURATION_MINUTES] as Long).toInt()

                WeekDate(weekDay, hour, minute, durationHours, durationMinutes)
            } catch (e: Exception) {
                Log.e(TAG, "toWeekDate: ", e)
                null
            }
        }

        fun createCurrentWeekDate(): WeekDate {
            val calendar = Calendar.getInstance()
            val weekDayNumber = if (calendar.get(Calendar.DAY_OF_WEEK) == 1) 7 else calendar.get(Calendar.DAY_OF_WEEK) - 1
            val weekDay = WeekDays.values().firstOrNull { it.id == weekDayNumber }

            return WeekDate(weekDay = weekDay!!, hour = calendar.get(Calendar.HOUR_OF_DAY), minute = calendar.get(Calendar.MINUTE))
        }
    }
}