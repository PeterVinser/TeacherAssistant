package com.piotrokninski.teacherassistant.viewmodel.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.adapteritem.CalendarAdapterItem
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreCourseRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMeetingRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreRecurringMeetingsRepository
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

            //New algorithm
            //Creating the list of meetings based on recurring meetings until the end date
            recurringMeetings?.forEach { recurringMeeting ->
                val attachedCourse = FirestoreCourseRepository.getCourse(recurringMeeting.courseId)

                val meetingDates = ArrayList<WeekDate.Companion.NumericalWeekDate>()
                recurringMeeting.meetingDates.forEach {
                    //Representing the meeting date as week day number (1,7) and time
                    meetingDates.add(WeekDate.Companion.NumericalWeekDate.toWeekDateSnapshot(it))
                }

                meetingDates.sortWith(
                    compareBy(
                        { it.numericalWeekDate },
                        { it.hour },
                        { it.minute })
                )

                //Queue used for queuing week dates
                val meetingDatesQueue: Queue<WeekDate.Companion.NumericalWeekDate> = LinkedList()
                meetingDatesQueue.addAll(meetingDates)

                val title = attachedCourse?.subject ?: "Spotkanie"

                var nextDate = recurringMeeting.date

                calendar.time = nextDate
                //For some reason the day of week starts with sunday (sunday->1)????
                val nextDateWeekDateNumerical =
                    if (calendar.get(Calendar.DAY_OF_WEEK) == 1) 7 else calendar.get(Calendar.DAY_OF_WEEK) - 1
                Log.d(TAG, "getMeetings: $title")
                Log.d(TAG, "getMeetings: $nextDate")
                Log.d(TAG, "getMeetings: $nextDateWeekDateNumerical")

                //Ordering the queue to begin match the week day with the week day of the next date
                for (i in 1..meetingDatesQueue.size) {
                    val polledWeekDate = meetingDatesQueue.peek()
                    Log.d(TAG, "getMeetings: ${polledWeekDate!!.numericalWeekDate}")
                    if (nextDateWeekDateNumerical == polledWeekDate.numericalWeekDate) {
                        break
                    } else {
                        meetingDatesQueue.add(meetingDatesQueue.poll())
                    }
                }

                var weekDate = meetingDatesQueue.poll()
                meetingDatesQueue.add(weekDate)

                while (nextDate <= endDate) {
                    val nextWeekDate = meetingDatesQueue.poll()

                    val nextMeeting = Meeting(
                        recurringMeeting.courseId,
                        null,
                        recurringMeeting.attendeeIds,
                        title,
                        nextDate,
                        false,
                        title,
                        weekDate!!.durationHours,
                        weekDate.durationMinutes
                    )

                    if (nextMeeting !in meetings) {
                        meetings.add(nextMeeting)
                    }

                    calendar.time = nextDate

                    var daysToAdd = nextWeekDate!!.numericalWeekDate - weekDate.numericalWeekDate
                    if (daysToAdd <= 0) {
                        daysToAdd += 7
                    }
                    calendar.add(Calendar.DAY_OF_WEEK, daysToAdd)
                    calendar.set(Calendar.HOUR_OF_DAY, nextWeekDate.hour)
                    calendar.set(Calendar.MINUTE, nextWeekDate.minute)

                    weekDate = nextWeekDate

                    nextDate = calendar.time
                    meetingDatesQueue.add(weekDate)
                    Log.d(TAG, "getMeetings: $nextDate")
                }
            }

            meetings.sortBy { it.date }

            var prevDate: Date? = null
            meetings.distinctBy { listOf(it.date, it.title) }.forEach { meeting ->
                if (prevDate != null) {
                    calendar.time = prevDate!!
                    val prevDay = calendar.get(Calendar.DAY_OF_YEAR)
                    val prevYear = calendar.get(Calendar.YEAR)

                    calendar.time = meeting.date
                    val day = calendar.get(Calendar.DAY_OF_YEAR)
                    val year = calendar.get(Calendar.YEAR)

                    if (prevDay != day || prevYear != year) {
                        addMeetingsHeader(meeting.date, auxList)
                    }
                } else {
                    addMeetingsHeader(meeting.date, auxList)
                }
                auxList.add(CalendarAdapterItem.MeetingAdapterItem(meeting))
                prevDate = meeting.date
            }

            _meetingItems.value = auxList
        }
    }

    private fun addMeetingsHeader(date: Date, list: ArrayList<CalendarAdapterItem>) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val weekDayItems = CalendarAdapterItem.HeaderAdapterItem.getHeaders()

        list.add(
            CalendarAdapterItem.HeaderAdapterItem(
                weekDayItems[calendar.get(
                    Calendar.DAY_OF_WEEK
                )]!!, calendar.time
            )
        )
    }
}