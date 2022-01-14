package com.piotrokninski.teacherassistant.model.user

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreUserHintContract
import java.lang.Exception

data class UserHint(val userId: String,
                    val fullName: String,
                    val keywords: ArrayList<String>) {
    companion object {

        fun DocumentSnapshot.toUserHint(): UserHint? {
            return try {

                val userId = getString(FirestoreUserHintContract.USER_ID)!!
                val fullName = getString(FirestoreUserHintContract.FULL_NAME)!!
                val keywords = get(FirestoreUserHintContract.KEYWORDS) as ArrayList<String>

                UserHint(userId, fullName, keywords)

            } catch (e: Exception) {
                Log.e(TAG, "toUserHint: ", e)
                null
            }
        }

        fun createHint(userId: String, fullName: String): UserHint {

            val keywords = ArrayList<String>()

            for (i in 1..fullName.length) {
                val keyword = fullName.lowercase().slice(0 until i)
                keywords.add(keyword)
            }

            return UserHint(userId, fullName, keywords)
        }

        private const val TAG = "UserHint"
    }
}
