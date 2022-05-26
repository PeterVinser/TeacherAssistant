package com.piotrokninski.teacherassistant.model.chat

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.getField

data class Chat(
    val userIds: List<String>,
) {


    companion object {

        fun DocumentSnapshot.toChat(): Chat? {
            return try {
//                Chat(
//                    get()
//                )
                null
            } catch (e: Exception) {
                Log.e(TAG, "toChat: ", e)
                null
            }
        }

        // Contract
        const val COLLECTION_NAME = "chats"

        const val USER_IDS = "userIds"
        const val LATEST_MESSAGE = "latestMessage"

        private const val TAG = "Chat"
    }
}
