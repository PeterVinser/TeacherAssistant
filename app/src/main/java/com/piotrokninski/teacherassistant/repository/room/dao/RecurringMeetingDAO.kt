package com.piotrokninski.teacherassistant.repository.room.dao

import androidx.room.*
import com.piotrokninski.teacherassistant.model.meeting.RecurringMeeting

@Dao
interface RecurringMeetingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurringMeeting(recurringMeeting: RecurringMeeting): Long

    @Update
    suspend fun updateRecurringMeeting(recurringMeeting: RecurringMeeting): Int

    @Delete
    suspend fun deleteRecurringMeeting(recurringMeeting: RecurringMeeting): Int

    @Query("SELECT * FROM ${RecurringMeeting.TABLE_NAME}")
    suspend fun getAllRecurringMeetings(): List<RecurringMeeting>
}