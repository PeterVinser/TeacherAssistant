package com.piotrokninski.teacherassistant.model.contract.firestore

object FirestoreFriendInvitationContract {
    const val COLLECTION_NAME = "friendInvitations"

    const val INVITATION_ID = "invitationId"

    const val INVITING_USER_ID = "invitingUserId"
    const val INVITING_USER_FULL_NAME = "invitingUserFullName"
    const val INVITED_USER_ID = "invitedUserId"
    const val INVITED_USER_FULL_NAME = "invitedUserFullName"

    const val INVITATION_TYPE = "invitationType"
    const val TYPE_STUDENT = "student"
    const val TYPE_TUTOR = "tutor"
    const val TYPE_FRIEND = "friend"

    const val STATUS = "status"
    const val STATUS_PENDING = "pending"
    const val STATUS_APPROVED = "approved"
    const val STATUS_REJECTED = "rejected"

    const val INVITATION_MESSAGE = "invitationMessage"

    const val COURSE = "course"
}