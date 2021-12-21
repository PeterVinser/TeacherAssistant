package com.piotrokninski.teacherassistant.model

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.FirestoreUserContract

//Data class is commonly used when a class' purpose is to hold data
data class User(val userId: String,
                var fullName: String,
                var username: String,
                var email: String,
                var student: Boolean,
                var tutor: Boolean,
                var subjects: String?,
                var localization: String?,
                var localizationRange: Double?,
                var preferredPrice: String?,
                var summary: String) {

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