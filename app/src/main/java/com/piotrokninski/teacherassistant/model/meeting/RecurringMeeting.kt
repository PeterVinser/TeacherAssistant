package com.piotrokninski.teacherassistant.model.meeting

import com.piotrokninski.teacherassistant.util.WeekDate
import java.util.*
import kotlin.collections.ArrayList

class RecurringMeeting(
    val courseId: String,
    var date: Date,
    var durationHours: Int,
    var durationMinutes: Int,
    val meetingDates: ArrayList<WeekDate>
)