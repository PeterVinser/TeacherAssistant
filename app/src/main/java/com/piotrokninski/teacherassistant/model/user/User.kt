package com.piotrokninski.teacherassistant.model.user

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.user.User.Companion.TABLE_NAME
import java.io.Serializable

@Entity(tableName = TABLE_NAME)
data class User(

    @PrimaryKey
    @ColumnInfo(name = ROOM_USER_ID)
    val userId: String,

    var fullName: String,
    var username: String,
    var email: String,
    var student: Boolean,
    var tutor: Boolean,
    var subjects: String?,
    var localization: String?,
    var localizationRange: Double?,
    var preferredPrice: String?,
    var summary: String

) : Serializable {

    fun getProfession(): String {
        return if (student && tutor) {
            "Uczeń/Korepetytor"
        } else if (student) {
            "Uczeń"
        } else if (tutor) {
            "Korepetytor"
        } else {
            "Nikt"
        }
    }

    companion object {
        fun DocumentSnapshot.toUser(): User? {
            return try {
                User(
                    getString(USER_ID)!!,
                    getString(FULL_NAME)!!,
                    getString(USERNAME)!!,
                    getString(EMAIL)!!,
                    getBoolean(STUDENT)!!,
                    getBoolean(TUTOR)!!,
                    getString(SUBJECTS),
                    getString(LOCALIZATION),
                    getDouble(LOCALIZATION_RANGE),
                    getString(PREFERRED_PRICE),
                    getString(SUMMARY)!!
                )
            } catch (e: Exception) {
                Log.e(TAG, "toUser: error: ", e)
                null
            }
        }

        //Contract
        const val COLLECTION_NAME = "users"
        const val TABLE_NAME = "user_table_name"

        const val ROOM_USER_ID = "user_id"

        private const val USER_ID = "userId"
        private const val FULL_NAME = "fullName"
        private const val USERNAME = "username"
        private const val EMAIL = "email"
        private const val TUTOR = "tutor"
        private const val STUDENT = "student"
        private const val SUBJECTS = "subjects"
        private const val LOCALIZATION = "localization"
        private const val LOCALIZATION_RANGE = "localizationRange"
        private const val PREFERRED_PRICE = "preferredPrice"
        private const val SUMMARY = "summary"

        private const val TAG = "User"
    }
}