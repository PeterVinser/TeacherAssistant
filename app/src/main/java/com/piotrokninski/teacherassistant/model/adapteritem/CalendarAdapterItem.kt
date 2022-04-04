package com.piotrokninski.teacherassistant.model.adapteritem

import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import java.util.*

sealed class CalendarAdapterItem {

    abstract val id: String
    abstract val date: Date

    data class HeaderAdapterItem(val titleId: Int, override val date: Date): CalendarAdapterItem() {
        override val id = titleId.toString()

        companion object {
            fun getHeaders(): Map<Int, Int> {
                return mapOf(
                    Calendar.MONDAY to R.string.monday_full,
                    Calendar.TUESDAY to R.string.tuesday_full,
                    Calendar.WEDNESDAY to R.string.wednesday_full,
                    Calendar.THURSDAY to R.string.thursday_full,
                    Calendar.FRIDAY to R.string.friday_full,
                    Calendar.SATURDAY to R.string.saturday_full,
                    Calendar.SUNDAY to R.string.sunday_full
                )
            }
        }
    }

    data class MeetingAdapterItem(val meeting: Meeting): CalendarAdapterItem() {
        override val id = meeting.date.toString()
        override val date = meeting.date
    }
}