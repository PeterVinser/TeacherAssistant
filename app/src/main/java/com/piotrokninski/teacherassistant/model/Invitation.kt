package com.piotrokninski.teacherassistant.model

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.piotrokninski.teacherassistant.model.course.Course
import java.io.Serializable

data class Invitation(
    var id: String? = null,
    val invitingUserId: String,
    val invitingUserFullName: String,
    var invitedUserId: String? = null,
    var invitedUserFullName: String? = null,
    val invitedAs: String,
    val type: String,
    var course: Course? = null,
    var meeting: Meeting? = null,
    var status: String = Contract.STATUS_PENDING,
    var message: String? = null,
    var chatId: String? = null
): Serializable {

    @get:Exclude
    val isComplete: Boolean
        get() =
            (invitedUserId != null && invitedUserFullName != null)
                    && (course != null == (course?.isComplete == true)
                    && (meeting != null == (meeting?.isComplete == true)))


    companion object {

        fun DocumentSnapshot.toInvitation() : Invitation? {
            return try {
                Invitation(
                    getString(Contract.ID),
                    getString(Contract.INVITING_USER_ID)!!,
                    getString(Contract.INVITING_USER_FULL_NAME)!!,
                    getString(Contract.INVITED_USER_ID)!!,
                    getString(Contract.INVITED_USER_FULL_NAME)!!,
                    getString(Contract.INVITED_AS)!!,
                    getString(Contract.TYPE)!!,
                    get(Contract.COURSE)?.let { Course.toCourse(it as Map<String, Any>) },
                    get(Contract.MEETING)?.let { Meeting.toMeeting(it as Map<String, Any>) },
                    getString(Contract.STATUS)!!,
                    getString(Contract.MESSAGE),
                    getString(Contract.CHAT_ID)
                )
            } catch (e: Exception) {
                Log.e(TAG, "toInvitation: ", e)
                null
            }
        }

        private const val TAG = "Invitation"
    }


    object Contract {

        // Contract
        const val COLLECTION_NAME = "invitations"

        const val ID = "id"
        const val INVITING_USER_ID = "invitingUserId"
        const val INVITING_USER_FULL_NAME = "invitingUserFullName"
        const val INVITED_USER_ID = "invitedUserId"
        const val INVITED_USER_FULL_NAME = "invitedUserFullName"
        const val INVITED_AS = "invitedAs"
        const val TYPE = "type"
        const val COURSE = "course"
        const val MEETING = "meeting"
        const val STATUS = "status"
        const val MESSAGE = "message"
        const val CHAT_ID = "chatId"

        const val STATUS_PENDING = "pending"
        const val STATUS_APPROVED = "approved"
        const val STATUS_REJECTED = "rejected"

        const val STUDENT = "student"
        const val TUTOR = "tutor"
        const val FRIEND = "friend"

        const val TYPE_FRIENDSHIP = "friendship"
        const val TYPE_COURSE = "course"
        const val TYPE_MEETING = "meeting"
    }
}
