package com.piotrokninski.teacherassistant.repository.room.dao

import androidx.room.*
import com.piotrokninski.teacherassistant.model.Meeting

@Dao
interface MeetingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: Meeting): Long

    @Update
    suspend fun updateMeeting(meeting: Meeting): Int

    @Delete
    suspend fun deleteMeeting(meeting: Meeting): Int

    @Query("DELETE FROM ${Meeting.Contract.TABLE_NAME} WHERE ${Meeting.Contract.DATE} < :date")
    suspend fun deleteMeetingsBefore(date: Long)

    @Query("DELETE FROM ${Meeting.Contract.TABLE_NAME}")
    suspend fun deleteAllMeetings()

    @Query("SELECT * FROM ${Meeting.Contract.TABLE_NAME}")
    suspend fun getAllMeetings(): List<Meeting>
}