package com.piotrokninski.teacherassistant.model.friend

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import java.io.Serializable

data class FriendInvitation(
    val invitingUserId: String,
    val invitingUserFullName: String,
    val invitedUserId: String,
    val invitedUserFullName: String,
    val invitationType: String,
    var invitationMessage: String?,
    var courseIds: ArrayList<String>?
) : Serializable {

    companion object {
        fun DocumentSnapshot.toFriendInvitation(): FriendInvitation? {
            return try {
                val invitingUserId = getString(FirestoreFriendInvitationContract.INVITING_USER_ID)
                val invitingUserFullName =
                    getString(FirestoreFriendInvitationContract.INVITING_USER_FULL_NAME)
                val invitedUserId = getString(FirestoreFriendInvitationContract.INVITED_USER_ID)
                val invitedUserFullName =
                    getString(FirestoreFriendInvitationContract.INVITED_USER_FULL_NAME)
                val invitationType = getString(FirestoreFriendInvitationContract.INVITATION_TYPE)
                val invitationMessage =
                    getString(FirestoreFriendInvitationContract.INVITATION_MESSAGE)

                val courseIds =
                    get(FirestoreFriendInvitationContract.COURSE_IDS) as ArrayList<String>?

                Log.d(TAG, "toFriendInvitation: called")

                Log.d(TAG, "toFriendInvitation: ${toString()}")

                if (invitingUserId != null && invitedUserId != null) {
                    Log.d(TAG, "toFriendInvitation: succeeded")

                    FriendInvitation(
                        invitingUserId,
                        invitingUserFullName!!,
                        invitedUserId,
                        invitedUserFullName!!,
                        invitationType!!,
                        invitationMessage,
                        courseIds
                    )
                } else {
                    null
                }

            } catch (e: Exception) {
                Log.e(TAG, "toFriend: ", e)
                null
            }
        }

        private const val TAG = "Friend"
    }
}