package com.piotrokninski.teacherassistant.model.friend

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

data class Friend(
    val userId: String,
    val fullName: String,
    val status: String,
    val friendshipType: String,
    val invitationId: String? = null
) {

    companion object {
        fun DocumentSnapshot.toFriend(): Friend? {
            return try {
                Friend(
                    getString(USER_ID)!!,
                    getString(FULL_NAME)!!,
                    getString(STATUS)!!,
                    getString(FRIENDSHIP_TYPE)!!,
                    getString(INVITATION_ID)
                )
            } catch (e: Exception) {
                Log.e(TAG, "toFriend: ", e)
                null
            }
        }

        //Contract
        const val COLLECTION_NAME = "friends"

        private const val USER_ID = "userId"
        private const val FULL_NAME = "fullName"
        const val STATUS = "status"
        const val FRIENDSHIP_TYPE = "friendshipType"
        private const val INVITATION_ID = "invitationId"
        const val STATUS_APPROVED = "approved"
        //Set when the friend is being invited
        const val STATUS_INVITED = "invited"
        //Set when the friend is inviting
        const val STATUS_INVITING = "inviting"
        const val STATUS_BLOCKED = "blocked"
        const val STATUS_BLANK = "blank"

        const val TYPE_STUDENT = "student"
        const val TYPE_TUTOR = "tutor"
        const val TYPE_FRIEND = "friend"
        const val TYPE_ALL = "all"

        private const val TAG = "Friend"
    }
}
