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
    fun weekDateToString(weekDate: WeekDate): String =
        "${weekDate.weekDay.name},${weekDate.hour},${weekDate.minute}," +
                "${weekDate.durationHours},${weekDate.durationMinutes}," +
                weekDate.timeZone

    @TypeConverter
    fun stringToWeekDate(string: String): WeekDate = run {
        val weekDateArray = string.split(",")
        return WeekDate(
            WeekDays.valueOf(weekDateArray[0]),
            weekDateArray[1].toInt(),
            weekDateArray[2].toInt(),
            weekDateArray[3].toInt(),
            weekDateArray[4].toInt(),
            weekDateArray[5]
        )
    }
}