package com.piotrokninski.teacherassistant.model.friend

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract

data class Friend(val userId: String,
                  val fullName: String,
                  val status: String,
                  val friendshipType: String) {

    companion object {
        fun DocumentSnapshot.toFriend(): Friend? {
            return try {
                val userId = getString(FirestoreFriendContract.USER_ID)!!
                val fullName = getString(FirestoreFriendContract.FULL_NAME)!!
                val status = getString(FirestoreFriendContract.STATUS)!!
                val friendshipType = getString(FirestoreFriendContract.FRIENDSHIP_TYPE)!!

                Friend(userId, fullName, status, friendshipType)
            } catch (e: Exception) {
                Log.e(TAG, "toFriend: ", e)
                null
            }
        }

        private const val TAG = "Friend"
    }
}
