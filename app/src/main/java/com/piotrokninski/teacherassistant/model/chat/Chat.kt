package com.piotrokninski.teacherassistant.model.chat

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.getField
import com.piotrokninski.teacherassistant.model.chat.Message.Companion.toMessage
import java.util.*

data class Chat(
    val id: String?,
    val users: Map<String, String>,
    val fullNames: Map<String, String>,
    val latestMessage: Message?,
    val read: Boolean
) {

    companion object {
        fun DocumentSnapshot.toChat(): Chat? {
            return try {

                val latestMessage = toMessage(get(Contract.LATEST_MESSAGE) as Map<String, Any>)

                Chat(
                    getString(Contract.ID),
                    get(Contract.USERS) as Map<String, String>,
                    get(Contract.FULL_NAMES) as Map<String, String>,
                    latestMessage,
                    getBoolean(Contract.READ)!!
                )
            } catch (e: Exception) {
                Log.e(TAG, "toChat: ", e)
                null
            }
        }

        private const val TAG = "Chat"
    }

    object Contract {
        const val COLLECTION_NAME = "chats"

        const val ID = "id"
        const val USERS = "users"
        const val FULL_NAMES = "fullNames"
        const val LATEST_MESSAGE = "latestMessage"
        const val READ = "read"

        const val STUDENT = "student"
        const val TUTOR = "tutor"
    }
}
