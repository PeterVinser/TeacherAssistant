package com.piotrokninski.teacherassistant.repository.room

import android.util.Log
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.piotrokninski.teacherassistant.util.WeekDate
import com.piotrokninski.teacherassistant.util.WeekDays
import java.util.*
import kotlin.collections.ArrayList

/**
 * The class allows Room Database to convert custom type fields automatically
 */
class Converters {
    private val TAG = "Converters"

    @TypeConverter
    fun timestampToDate(value: Long?): Date? =
        value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? =
        date?.time

    @TypeConverter
    fun arrayListToString(list: List<String>): String =
        list.joinToString(separator = ";") { it }

    @TypeConverter
    fun stringToList(string: String?): List<String>? =
        string?.split(";")

    @TypeConverter
    fun listOfWeekDateToString(weekDates: List<WeekDate>): String = run {
        val stringArray = ArrayList<String>()

        weekDates.forEach {
            stringArray.add(weekDateToString(it))
        }

        return arrayListToString(stringArray)
    }

    @TypeConverter
    fun stringToListOfWeekDates(string: String): List<WeekDate> = run {
        val weekDates = ArrayList<WeekDate>()

        stringToList(string)?.forEach {
            weekDates.add(stringToWeekDate(it))
        }

        return weekDates
    }

    private fun weekDateToString(weekDate: WeekDate): String =
        "${weekDate.weekDay.name},${weekDate.hour},${weekDate.minute}," +
                "${weekDate.durationHours},${weekDate.durationMinutes}," +
                "${weekDate.timeZone},${weekDate.calendarId}"

    private fun stringToWeekDate(string: String): WeekDate = run {
        val weekDateArray = string.split(",")
        return WeekDate(
            WeekDays.valueOf(weekDateArray[0]),
            weekDateArray[1].toInt(),
            weekDateArray[2].toInt(),
            weekDateArray[3].toInt(),
            weekDateArray[4].toInt(),
            weekDateArray[5],
            weekDateArray[6].toLongOrNull()
        )
    }
}