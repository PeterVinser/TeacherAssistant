package com.piotrokninski.teacherassistant.repository.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.piotrokninski.teacherassistant.model.User
import com.piotrokninski.teacherassistant.repository.room.dao.UserDAO
import java.lang.NullPointerException

@Database(entities = [User::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract val userDao: UserDAO

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