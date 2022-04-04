package com.piotrokninski.teacherassistant.viewmodel.main

import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.model.adapteritem.CalendarAdapterItem
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMeetingRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreRecurringMeetingsRepository
import com.piotrokninski.teacherassistant.repository.sharedpreferences.MainPreferences
import com.piotrokninski.teacherassistant.util.WeekDate
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragmentViewModel : ViewModel() {
    private val TAG = "CalendarFragmentViewMod"
    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val _meetingItems = MutableLiveData<ArrayList<CalendarAdapterItem>>()
    val meetings: LiveData<ArrayList<CalendarAdapterItem>> = _meetingItems

    init {
        getMeetings()
    }

    private fun getMeetings() {
        viewModelScope.launch {
            val auxList = ArrayList<CalendarAdapterItem>()

            val weekDayItems = CalendarAdapterItem.HeaderAdapterItem.getHeaders()

            //Get first half a year of meetings
            val calendar = Calendar.getInstance()
            val startDate = calendar.time
            calendar.add(Calendar.MONTH, 6)
            val endDate = calendar.time

            val meetings = FirestoreMeetingRepository.getMeetingsFromRange(
                currentUserId,
                startDate,
                endDate
            ) ?: ArrayList()

            //Meetings ordered by date already by query
            val recurringMeetings =
                FirestoreRecurringMeetingsRepository.getRecurringMeetings(currentUserId)

            if (!recurringMeetings.isNullOrEmpty()) {
                //Creating and populating the collection for storing next dates of the given recurring meetings
                val recurringMeetingDates = ArrayList<Date>()

                val attachedCourses = HashMap<String, Course>()

                recurringMeetings.forEach {
                    recurringMeetingDates.add(it.date)
                    val attachedCourse = FirestoreCourseRepository.getCourse(it.courseId)
                    if (attachedCourse != null) attachedCourses[it.courseId] = attachedCourse
                }

                //The date to be inserted into the auxList
                var nextDate = recurringMeetingDates.first()

                while (nextDate < endDate) {
                    val nextRecurringMeeting =
                        recurringMeetings[recurringMeetingDates.indexOf(nextDate)]

                    val courseId = nextRecurringMeeting.courseId

                    val title = attachedCourses[courseId]?.subject ?: "Spotkanie"

                    val nextMeeting = Meeting(
                        courseId,
                        null,
                        nextRecurringMeeting.attendeeIds,
                        title,
                        nextDate,
                        false,
                        title,
                        nextRecurringMeeting.durationHours,
                        nextRecurringMeeting.durationMinutes
                    )

                    if (nextMeeting !in meetings) {
                        meetings.add(nextMeeting)
                    }
                    var dateToUpdate: Date? = null

                    nextRecurringMeeting.meetingDates.forEach { date ->
                        val weekDay = date.weekDay
                        var hour = date.hour
                        var minute = date.minute
                        val offset = date.offset

                        val offsetSign = offset.substring(0, 1)
                        val offsetHour = offset.substring(1, 3).toInt()
                        val offsetMinute = offset.substring(4).toInt()

                        //Represent the hour in UTC format
//                        if (hour != null && minute != null) {
//                            if (offsetSign == "+") {
//                                hour -= offsetHour
//                                minute -= offsetMinute
//                            }
//                            if (offsetSign == "-") {
//                                hour += offsetHour
//                                minute += offsetMinute
//                            }
//                        }

                        val weekDayNumber = WeekDate.WEEK_DAYS_NUMERICAL[weekDay]!!
                        calendar.time = nextDate
                        //For some reason the day of week starts with sunday (sunday->1)????
                        calendar.add(
                            Calendar.DAY_OF_WEEK,
                            (weekDayNumber + (7 - (calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7)) % 7
                        )
                        calendar.set(Calendar.HOUR_OF_DAY, hour!!)
                        calendar.set(Calendar.MINUTE, minute!!)

                        if (dateToUpdate == null) {
                            dateToUpdate = calendar.time
                        }

                        if (nextDate >= dateToUpdate) {
                            calendar.add(Calendar.DAY_OF_WEEK, 7)
                            dateToUpdate = calendar.time
                        }

                        if (calendar.time < dateToUpdate) {
                            dateToUpdate = calendar.time
                        }
                    }

                    //In the event of an error, blank nextDate or any other problem nextDate is assigned as endDate in order to break the loop
                    recurringMeetingDates[recurringMeetingDates.indexOf(nextDate)] =
                        dateToUpdate ?: endDate
                    nextDate = recurringMeetingDates.minOrNull() ?: endDate
                }
            }

            meetings.sortBy { it.date }

            var prevDate: Date? = null
            meetings.distinctBy { listOf(it.date, it.title) }.forEach { meeting ->
                if (prevDate != null && (prevDate!!.time - meeting.date.time) / (24 * 60 * 60 * 1000) != (0.toLong())) {
                    calendar.time = meeting.date
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    auxList.add(
                        CalendarAdapterItem.HeaderAdapterItem(
                            weekDayItems[calendar.get(
                                Calendar.DAY_OF_WEEK
                            )]!!, calendar.time
                        )
                    )
                }
                auxList.add(CalendarAdapterItem.MeetingAdapterItem(meeting))
                prevDate = meeting.date
            }

            _meetingItems.value = auxList
        }
    }
}