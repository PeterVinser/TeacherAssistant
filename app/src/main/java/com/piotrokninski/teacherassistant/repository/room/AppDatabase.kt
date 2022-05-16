package com.piotrokninski.teacherassistant.repository.room

import android.content.Context
import androidx.room.*
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import com.piotrokninski.teacherassistant.model.meeting.RecurringMeeting
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.repository.room.dao.MeetingDAO
import com.piotrokninski.teacherassistant.repository.room.dao.RecurringMeetingDAO
import com.piotrokninski.teacherassistant.repository.room.dao.UserDAO
import java.lang.NullPointerException

@Database(
    entities = [User::class, Meeting::class, RecurringMeeting::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2 )]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val userDao: UserDAO
    abstract val meetingDAO: MeetingDAO
    abstract val recurringMeetingDAO: RecurringMeetingDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun instantiate(context: Context) {
            //Assures that if one thread uses the database no other can access it
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).build()
                }
                INSTANCE = instance
            }
        }

        fun getInstance(): AppDatabase {
            synchronized(this) {
                return INSTANCE
                    ?: throw NullPointerException("The database has not been instantiated")
            }
        }
    }
}