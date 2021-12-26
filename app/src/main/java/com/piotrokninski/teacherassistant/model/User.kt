package com.piotrokninski.teacherassistant.model

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreUserContract
import com.piotrokninski.teacherassistant.model.contract.room.RoomUserContract
import java.io.Serializable

@Entity(tableName = RoomUserContract.TABLE_NAME)
data class User (

    @PrimaryKey
    @ColumnInfo(name = RoomUserContract.USER_ID)
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
                val userId = getString(FirestoreUserContract.USER_ID)!!
                val fullName = getString(FirestoreUserContract.FULL_NAME)!!
                val username = getString(FirestoreUserContract.USERNAME)!!
                val email = getString(FirestoreUserContract.EMAIL)!!
                val isStudent = getBoolean(FirestoreUserContract.STUDENT)!!
                val isTutor = getBoolean(FirestoreUserContract.TUTOR)!!
                val subjects = getString(FirestoreUserContract.SUBJECTS)
                val localization = getString(FirestoreUserContract.LOCALIZATION)
                val localizationRange = getDouble(FirestoreUserContract.LOCALIZATION_RANGE)
                val preferredPrice = getString(FirestoreUserContract.PREFERRED_PRICE)
                val summary = getString(FirestoreUserContract.SUMMARY)!!

                User(userId, fullName, username, email, isStudent, isTutor,
                    subjects, localization, localizationRange, preferredPrice, summary)
            } catch (e: Exception) {
                Log.e(TAG, "toUser: error: ", e)
                null
            }
        }

        private const val TAG = "User"
    }
}