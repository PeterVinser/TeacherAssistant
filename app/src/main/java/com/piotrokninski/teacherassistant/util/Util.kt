package com.piotrokninski.teacherassistant.util

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

object Util {
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
}