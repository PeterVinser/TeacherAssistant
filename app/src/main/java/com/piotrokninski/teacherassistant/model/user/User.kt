package com.piotrokninski.teacherassistant.model.user

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable

@Entity(tableName = User.Contract.TABLE_NAME)
data class User(

    @PrimaryKey
    @ColumnInfo(name = Contract.ROOM_USER_ID)
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
                    getString(Contract.USER_ID)!!,
                    getString(Contract.FULL_NAME)!!,
                    getString(Contract.USERNAME)!!,
                    getString(Contract.EMAIL)!!,
                    getBoolean(Contract.STUDENT)!!,
                    getBoolean(Contract.TUTOR)!!,
                    getString(Contract.SUBJECTS),
                    getString(Contract.LOCALIZATION),
                    getDouble(Contract.LOCALIZATION_RANGE),
                    getString(Contract.PREFERRED_PRICE),
                    getString(Contract.SUMMARY)!!
                )
            } catch (e: Exception) {
                Log.e(TAG, "toUser: error: ", e)
                null
            }
        }

        private const val TAG = "User"
    }
    
    object Contract {
        const val COLLECTION_NAME = "users"
        const val TABLE_NAME = "user_table_name"

        const val ROOM_USER_ID = "user_id"

        const val USER_ID = "userId"
        const val FULL_NAME = "fullName"
        const val USERNAME = "username"
        const val EMAIL = "email"
        const val TUTOR = "tutor"
        const val STUDENT = "student"
        const val SUBJECTS = "subjects"
        const val LOCALIZATION = "localization"
        const val LOCALIZATION_RANGE = "localizationRange"
        const val PREFERRED_PRICE = "preferredPrice"
        const val SUMMARY = "summary"
    }
}