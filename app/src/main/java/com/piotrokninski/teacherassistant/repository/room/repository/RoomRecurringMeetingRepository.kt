package com.piotrokninski.teacherassistant.repository.room.repository

import com.piotrokninski.teacherassistant.model.meeting.RecurringMeeting
import com.piotrokninski.teacherassistant.repository.room.dao.RecurringMeetingDAO

class RoomRecurringMeetingRepository(private val dao: RecurringMeetingDAO) {

    suspend fun insertRecurringMeeting(recurringMeeting: RecurringMeeting): Long {
        return dao.insertRecurringMeeting(recurringMeeting)
    }

    suspend fun updateRecurringMeeting(recurringMeeting: RecurringMeeting): Int {
        return dao.updateRecurringMeeting(recurringMeeting)
    }

    suspend fun deleteRecurringMeetings(meetings: ArrayList<RecurringMeeting>): ArrayList<Int> {
        val ids = ArrayList<Int>()
        meetings.forEach {
            dao.deleteRecurringMeeting(it)
        }

        return ids
    }

    suspend fun getAllRecurringMeetings(): List<RecurringMeeting> {
        return dao.getAllRecurringMeetings()
    }
}