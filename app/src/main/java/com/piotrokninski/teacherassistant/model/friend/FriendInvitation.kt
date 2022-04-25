package com.piotrokninski.teacherassistant.model.friend

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
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
    var status: String = FirestoreFriendInvitationContract.STATUS_PENDING,
    var invitationMessage: String?,
    var course: Course?
) : Serializable {

    companion object {
        fun DocumentSnapshot.toFriendInvitation(): FriendInvitation? {
            return try {
                val invitationId = getString(FirestoreFriendInvitationContract.INVITATION_ID)
                val invitingUserId = getString(FirestoreFriendInvitationContract.INVITING_USER_ID)!!
                val invitingUserFullName =
                    getString(FirestoreFriendInvitationContract.INVITING_USER_FULL_NAME)!!
                val invitedUserId = getString(FirestoreFriendInvitationContract.INVITED_USER_ID)!!
                val invitedUserFullName =
                    getString(FirestoreFriendInvitationContract.INVITED_USER_FULL_NAME)!!
                val invitationType = getString(FirestoreFriendInvitationContract.INVITATION_TYPE)!!
                val status = getString(FirestoreFriendInvitationContract.STATUS)!!
                val invitationMessage =
                    getString(FirestoreFriendInvitationContract.INVITATION_MESSAGE)
                val course =
                    get(FirestoreFriendInvitationContract.COURSE)?.let { toCourse(it as Map<String, Any>) }

                FriendInvitation(
                    invitationId,
                    invitingUserId,
                    invitingUserFullName,
                    invitedUserId,
                    invitedUserFullName,
                    invitationType,
                    status,
                    invitationMessage,
                    course
                )
            } catch (e: Exception) {
                Log.e(TAG, "toFriend: ", e)
                null
            }
        }

        private const val TAG = "Friend"
    }
}