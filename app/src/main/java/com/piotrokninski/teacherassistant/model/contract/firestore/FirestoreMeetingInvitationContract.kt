package com.piotrokninski.teacherassistant.model.contract.firestore

object FirestoreMeetingInvitationContract {

    const val COLLECTION_NAME = "meetingInvitations"

    const val TITLE = "title"

    const val DESCRIPTION = "description"

    const val INVITING_USER_ID = "invitingUserId"
    const val INVITING_USER_FULL_NAME = "invitingUserFullName"

    const val INVITED_USER_ID = "invitedUserId"
    const val INVITED_USER_FULL_NAME = "invitedUserFullName"

    const val DURATION_HOURS = "durationHours"
    const val DURATION_MINUTES = "durationMinutes"

    const val DATE = "date"
    const val WEEK_DATE = "weekDate"

    const val MODE = "mode"

    const val STATUS = "status"
}