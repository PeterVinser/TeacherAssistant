package com.piotrokninski.teacherassistant.model.contract.firestore

object FirestoreFriendContract {
    const val COLLECTION_NAME = "friends"

    const val USER_ID = "userId"
    const val FULL_NAME = "fullName"

    const val STATUS = "status"
    const val STATUS_APPROVED = "approved"
    //Set when the friend is being invited
    const val STATUS_INVITED = "invited"
    //Set when the friend is inviting
    const val STATUS_INVITING = "inviting"
    const val STATUS_BLOCKED = "blocked"
    const val STATUS_BLANK = "blank"

    const val FRIENDSHIP_TYPE = "friendshipType"
    const val TYPE_STUDENT = "student"
    const val TYPE_TUTOR = "tutor"
    const val TYPE_FRIEND = "friend"
    const val TYPE_ALL = "all"

    const val INVITATION_ID = "invitationId"
}