package com.piotrokninski.teacherassistant.model.contract.firestore

object FirestoreFriendInvitationContract {
    const val COLLECTION_NAME = "friendInvitations"

    //New contract
    const val INVITING_USER_ID = "invitingUserId"
    const val INVITING_USER_FULL_NAME = "invitingUserFullName"
    const val INVITED_USER_ID = "invitedUserId"
    const val INVITED_USER_FULL_NAME = "invitedUserFullName"

    const val INVITATION_TYPE = "invitationType"
    const val TYPE_STUDENT = "student"
    const val TYPE_TUTOR = "tutor"
    const val TYPE_FRIEND = "friend"

    const val INVITATION_MESSAGE = "invitationMessage"

    const val COURSE_ID = "courseId"
}