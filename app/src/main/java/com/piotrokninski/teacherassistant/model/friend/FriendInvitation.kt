package com.piotrokninski.teacherassistant.model.friend

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendInvitationRepository
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreFriendRepository
import java.io.Serializable

data class FriendInvitation(val invitingUserId: String,
                            val invitingUserFullName: String,
                            val invitedUserId: String,
                            val invitedUserFullName: String,
                            val invitationType: String,
                            var invitationMessage: String?,
                            var courseIds: ArrayList<String>?): Serializable {

    companion object {
        fun DocumentSnapshot.toFriendInvitation(): FriendInvitation? {
            return try {
                val invitingUserId = getString(FirestoreFriendInvitationContract.INVITING_USER_ID)
                val invitingUserFullName = getString(FirestoreFriendInvitationContract.INVITING_USER_FULL_NAME)
                val invitedUserId = getString(FirestoreFriendInvitationContract.INVITED_USER_ID)
                val invitedUserFullName = getString(FirestoreFriendInvitationContract.INVITED_USER_FULL_NAME)
                val invitationType = getString(FirestoreFriendInvitationContract.INVITATION_TYPE)
                val invitationMessage = getString(FirestoreFriendInvitationContract.INVITATION_MESSAGE)
                val courseIds = get(FirestoreFriendInvitationContract.COURSE_IDS) as ArrayList<String>

                if (invitingUserId != null && invitedUserId != null) {
                    FriendInvitation(invitingUserId, invitingUserFullName!!, invitedUserId, invitedUserFullName!!,
                        invitationType!!, invitationMessage, courseIds)
                } else {
                    null
                }

            } catch (e: Exception) {
                Log.e(TAG, "toFriend: ", e)
                null
            }
        }

        fun sendInvitation(invitation: FriendInvitation) {

            val invitedFriend = Friend(invitation.invitedUserId, invitation.invitedUserFullName,
                FirestoreFriendContract.STATUS_INVITED, invitation.invitationType)

            val invitingFriend = Friend(invitation.invitingUserId, invitation.invitingUserFullName,
                FirestoreFriendContract.STATUS_INVITING, invitation.invitationType)

            FirestoreFriendInvitationRepository.setFriendInvitationData(invitation)

            FirestoreFriendRepository.setFriendData(invitation.invitingUserId, invitedFriend)
            FirestoreFriendRepository.setFriendData(invitation.invitedUserId, invitingFriend)
        }

        fun approveInvitation(invitation: FriendInvitation) {
            //The type of relationship assigned to invited user (the one requested in invitation)
            val invitedUserFriendshipType = invitation.invitationType
            //The type of relationship assigned to inviting user
            val invitingUserFriendshipType = when (invitedUserFriendshipType) {

                FirestoreFriendInvitationContract.TYPE_STUDENT -> FirestoreFriendContract.TYPE_TUTOR

                FirestoreFriendInvitationContract.TYPE_TUTOR -> FirestoreFriendContract.TYPE_STUDENT

                FirestoreFriendInvitationContract.TYPE_FRIEND -> FirestoreFriendContract.TYPE_FRIEND

                else -> {
                    throw IllegalArgumentException()
                }
            }

            FirestoreFriendRepository.updateFriendshipType(invitation.invitedUserId, invitation.invitingUserId, invitingUserFriendshipType)
            FirestoreFriendRepository.updateFriendshipStatus(invitation.invitedUserId, invitation.invitingUserId, FirestoreFriendContract.STATUS_APPROVED)

            FirestoreFriendRepository.updateFriendshipStatus(invitation.invitingUserId, invitation.invitedUserId, invitedUserFriendshipType)
            FirestoreFriendRepository.updateFriendshipStatus(invitation.invitingUserId, invitation.invitedUserId, FirestoreFriendContract.STATUS_APPROVED)

            FirestoreFriendInvitationRepository.deleteFriendInvitationData(invitation.invitedUserId, invitation.invitingUserId)
        }

        fun deleteInvitation(invitedUserId: String, invitingUserId: String) {
            FirestoreFriendInvitationRepository.deleteFriendInvitationData(invitedUserId, invitingUserId)

            FirestoreFriendRepository.deleteFriendData(invitedUserId, invitingUserId)
            FirestoreFriendRepository.deleteFriendData(invitingUserId, invitedUserId)
        }

        private const val TAG = "Friend"
    }
}