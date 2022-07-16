package com.piotrokninski.teacherassistant.repository.room.repository

import com.piotrokninski.teacherassistant.model.Meeting
import com.piotrokninski.teacherassistant.repository.room.dao.MeetingDAO

class RoomMeetingRepository(private val dao: MeetingDAO) {
    suspend fun insertMeeting(meeting: Meeting): Long {
        return dao.insertMeeting(meeting)
    }

    suspend fun insertMeetings(meetings: ArrayList<Meeting>): ArrayList<Long> {
        val ids = ArrayList<Long>()

        meetings.forEach { meeting ->
            ids.add(dao.insertMeeting(meeting))
        }

        return ids
    }

    suspend fun updateMeeting(meeting: Meeting): Int {
        return dao.updateMeeting(meeting)
    }

    suspend fun deleteMeetings(meetings: ArrayList<Meeting>): ArrayList<Int> {
        val ids = ArrayList<Int>()
        meetings.forEach {
            ids.add(dao.deleteMeeting(it))
        }

        return ids
    }

    suspend fun deleteMeetingsBefore(date: Long) {
        return dao.deleteMeetingsBefore(date)
    }

    suspend fun deleteAllMeetings() {
        return dao.deleteAllMeetings()
    }

    suspend fun getAllMeetings(): List<Meeting> {
        return dao.getAllMeetings()
    }
}