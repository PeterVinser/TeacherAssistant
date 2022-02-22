package com.piotrokninski.teacherassistant.util

import android.util.Log
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.StringBuilder

object RRule {
    //Using RFC2445 calendar standard
    private const val TAG = "RRule"

    private const val RRULE = "RRULE:"

    private const val FREQUENCY = "FREQ"

    private const val WEEKLY = "WEEKLY"

    private const val BY_DAY = "BYDAY"

    private const val INTERVAL = "INTERVAL"

    private val WEEK_DAYS = mapOf(
        WeekDays.MONDAY.toString() to "MO",
        WeekDays.TUESDAY.toString() to "TU",
        WeekDays.WEDNESDAY.toString() to "WE",
        WeekDays.THURSDAY.toString() to "TH",
        WeekDays.FRIDAY.toString() to "FR",
        WeekDays.SATURDAY.toString() to "SA",
        WeekDays.SUNDAY.toString() to "SU"
     )

    fun getWeeklyRuleString(weekDays: Array<String>): String {

        if (weekDays.isEmpty()) {
            throw IllegalArgumentException("No week days passed")
        }

        val weekDaysString = StringBuilder()

        weekDays.forEach { weekDay ->
            weekDaysString.append("${WEEK_DAYS[weekDay]},")
        }

        weekDaysString.deleteCharAt(weekDaysString.length - 1)

        return "$RRULE$FREQUENCY=$WEEKLY;$BY_DAY=$weekDaysString;$INTERVAL=1"
    }

    fun toMap(rrule: String): Map<String, String> {

        val rruleMap = mutableMapOf<String, String>()

        val items = rrule.replace(RRULE, "").split(";", "=")

        var i = 0

        try {
            while (i < items.size) {
                rruleMap[items[i++]] = items[i++]
            }
        } catch (e: Exception) {
            Log.e(TAG, "toMap: ", e)
        }

        return rruleMap
    }

    fun toString(rrule: Map<String, String>): String {
        val rruleString = StringBuilder()
        rruleString.append(RRULE)

        rrule.forEach {
            rruleString.append("${it.key}=${it.value};")
        }

        rruleString.deleteCharAt(rruleString.length - 1)

        return rruleString.toString()
    }
}