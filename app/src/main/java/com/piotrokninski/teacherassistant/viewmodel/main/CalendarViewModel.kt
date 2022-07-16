package com.piotrokninski.teacherassistant.viewmodel.main

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.piotrokninski.teacherassistant.model.Meeting
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreMeetingRepository
import com.piotrokninski.teacherassistant.util.WeekDate
import com.piotrokninski.teacherassistant.view.main.adapter.CalendarAdapter
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewModel : ViewModel() {
    private val TAG = "CalendarFragmentViewMod"
    private val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val _meetingItems = MutableLiveData<ArrayList<CalendarAdapter.Item>>()
    val meetings: LiveData<ArrayList<CalendarAdapter.Item>> = _meetingItems

    init {
        getMeetings()
    }

    private fun getMeetings() {
        viewModelScope.launch {
            val auxList = ArrayList<CalendarAdapter.Item>()

            //Get first half a year of meetings
            val calendar = Calendar.getInstance()
            val startDate = calendar.time
            calendar.add(Calendar.MONTH, 6)
            val endDate = calendar.time

            val meetings = FirestoreMeetingRepository.getMeetingsFromRange(
                currentUserId,
                startDate,
                endDate
            ) 
            
            if (meetings != null) {

                //Meetings ordered by date already by query
                createMeetings(meetings.filter { !it.singular }, endDate).let {
                    meetings.addAll(it)
                }

                meetings.sortBy { it.date }

                var prevDate: Date? = null
                meetings.forEach { meeting ->
                    if (prevDate != null) {
                        calendar.time = prevDate!!
                        val prevDay = calendar.get(Calendar.DAY_OF_YEAR)
                        val prevYear = calendar.get(Calendar.YEAR)

                        calendar.time = meeting.date!!
                        val day = calendar.get(Calendar.DAY_OF_YEAR)
                        val year = calendar.get(Calendar.YEAR)

                        if (prevDay != day || prevYear != year) {
                            addMeetingsHeader(meeting.date!!, auxList)
                        }
                    } else {
                        addMeetingsHeader(meeting.date!!, auxList)
                    }
                    auxList.add(CalendarAdapter.Item.Meeting(meeting))
                    prevDate = meeting.date
                }

                _meetingItems.value = auxList
            } else {
                _meetingItems.value = null
            }
        }
    }

    private fun createMeetings(recurringMeetings: List<Meeting>, endDate: Date): List<Meeting> {
        val meetings = ArrayList<Meeting>()

        val calendar = Calendar.getInstance()

        recurringMeetings.forEach { recurringMeeting ->
            if (recurringMeeting.date != null) {

                calendar.time = recurringMeeting.date!!
                calendar.add(Calendar.DAY_OF_WEEK, 7)

                var date = calendar.time

                while (date <= endDate) {

                    meetings.add(
                        Meeting(
                            recurringMeeting.id,
                            recurringMeeting.courseId,
                            lessonId = null,
                            recurringMeeting.attendeeIds!!,
                            recurringMeeting.title,
                            recurringMeeting.description,
                            date,
                            recurringMeeting.durationHours,
                            recurringMeeting.durationMinutes,
                            singular = false,
                            completed = false,
                        )
                    )

                    calendar.add(Calendar.DAY_OF_WEEK, 7)

                    date = calendar.time
                }
            }
        }

        return meetings
    }

    private fun addMeetingsHeader(date: Date, list: ArrayList<CalendarAdapter.Item>) {
        val calendar = Calendar.getInstance()

        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val weekDayItems = CalendarAdapter.Item.Header.getHeaders()

        list.add(
            CalendarAdapter.Item.Header(
                weekDayItems[calendar.get(
                    Calendar.DAY_OF_WEEK
                )]!!, calendar.time
            )
        )
    }

    class Factory: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                return CalendarViewModel() as T
            }
            throw IllegalArgumentException("View model not found")
        }
    }
}