package com.piotrokninski.teacherassistant.model.friend

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.course.Course.Companion.toCourse
import java.io.Serializable

data class FriendInvitation(
    var invitationId: String?,
    val invitingUserId: String,
    val invitingUserFullName: String,
    val invitedUserId: String,
    val invitedUserFullName: String,
    val invitationType: String,
    var status: String = STATUS_PENDING,
    var invitationMessage: String?,
    var course: Course?
) : Serializable {

    companion object {
        fun DocumentSnapshot.toFriendInvitation(): FriendInvitation? {
            return try {
                FriendInvitation(
                    getString(INVITATION_ID),
                    getString(INVITING_USER_ID)!!,
                    getString(INVITING_USER_FULL_NAME)!!,
                    getString(INVITED_USER_ID)!!,
                    getString(INVITED_USER_FULL_NAME)!!,
                    getString(TYPE)!!,
                    getString(STATUS)!!,
                    getString(INVITATION_MESSAGE),
                    get(COURSE)?.let { toCourse(it as Map<String, Any>) }
                )
            } catch (e: Exception) {
                Log.e(TAG, "toFriend: ", e)
                null
            }
        }

        //Contract
        const val COLLECTION_NAME = "friendInvitations"

        private const val INVITATION_ID = "invitationId"
        const val INVITING_USER_ID = "invitingUserId"
        private const val INVITING_USER_FULL_NAME = "invitingUserFullName"
        const val INVITED_USER_ID = "invitedUserId"
        private const val INVITED_USER_FULL_NAME = "invitedUserFullName"
        private const val INVITATION_MESSAGE = "invitationMessage"
        private const val COURSE = "course"
        private const val TYPE = "invitationType"
        const val STATUS = "status"

        const val STATUS_PENDING = "pending"
        const val STATUS_APPROVED = "approved"
        const val STATUS_REJECTED = "rejected"

        const val TYPE_STUDENT = "student"
        const val TYPE_TUTOR = "tutor"
        const val TYPE_FRIEND = "friend"

        private const val TAG = "Friend"
    }
}