package com.piotrokninski.teacherassistant.model.friend

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.chat.Message
import java.io.Serializable

data class Friend(
    val userId: String,
    val fullName: String,
    val status: String,
    val friendshipType: String,
    val invitationId: String? = null,
    val chatId: String,
    val latestMessage: Message? = null
) : Serializable {

    companion object {
        fun DocumentSnapshot.toFriend(): Friend? {
            return try {
                Friend(
                    getString(Contract.USER_ID)!!,
                    getString(Contract.FULL_NAME)!!,
                    getString(Contract.STATUS)!!,
                    getString(Contract.FRIENDSHIP_TYPE)!!,
                    getString(Contract.INVITATION_ID),
                    getString(Contract.CHAT_ID)!!
                )
            } catch (e: Exception) {
                Log.e(TAG, "toFriend: ", e)
                null
            }
        }

        private const val TAG = "Friend"
    }
    
    object Contract {

        const val COLLECTION_NAME = "friends"

        const val USER_ID = "userId"
        const val FULL_NAME = "fullName"
        const val STATUS = "status"
        const val FRIENDSHIP_TYPE = "friendshipType"
        const val INVITATION_ID = "invitationId"
        const val CHAT_ID = "chatId"

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
    }
}
