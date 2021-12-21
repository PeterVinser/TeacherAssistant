package com.piotrokninski.teacherassistant.model

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.FirestoreFriendContract
import com.piotrokninski.teacherassistant.repository.FirestoreFriendRepository
import java.lang.Exception

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

        fun deleteFriend(userId: String, friendId: String) {
            FirestoreFriendRepository.deleteFriendData(userId, friendId)
        }

        private const val TAG = "Friend"
    }
}
