package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.piotrokninski.teacherassistant.model.chat.Chat
import com.piotrokninski.teacherassistant.model.chat.Message
import com.piotrokninski.teacherassistant.model.chat.Message.Companion.toMessage
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.meeting.Meeting
import kotlinx.coroutines.tasks.await

object FirestoreMessageRepository {
    private const val TAG = "FirestoreMessageReposit"

    fun addMessage(chatId: String, message: Message) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Chat.COLLECTION_NAME).document(chatId)
            .collection(Message.COLLECTION_NAME).add(message)
    }

    fun markAsRead(chatId: String, messageId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Chat.COLLECTION_NAME).document(chatId)
            .collection(Message.COLLECTION_NAME).document(messageId)
            .update(Message.READ, true)
    }

    suspend fun getLatestMessage(chatId: String): Message? {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection(Chat.COLLECTION_NAME).document(chatId)
            .collection(Message.COLLECTION_NAME).orderBy(Message.TIMESTAMP, Query.Direction.DESCENDING)
            .limit(1)

        try {
            query.get().await().forEach { message ->
                message?.toMessage()?.let {
                    it.id = message.id
                    return it
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getLatestMessage: ", e)
        }

        return null
    }

    suspend fun getLatestMessages(chatId: String): ArrayList<Message>? {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection(Chat.COLLECTION_NAME).document(chatId)
            .collection(Message.COLLECTION_NAME).orderBy(Message.TIMESTAMP, Query.Direction.DESCENDING)
            .limit(30)

        return try {
            val messages = ArrayList<Message>()

            query.get().await().forEach { message ->
                message?.toMessage()?.let {
                    messages.add(it)
                }
            }

            messages.ifEmpty { null }
        } catch (e: Exception) {
            Log.e(TAG, "getLatestMessages: ", e)
            null
        }
    }

    suspend fun getMessagesBefore(chatId: String, time: Timestamp): ArrayList<Message>? {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection(Chat.COLLECTION_NAME).document(chatId)
            .collection(Message.COLLECTION_NAME).whereLessThan(Message.TIMESTAMP, time)
            .orderBy(Message.TIMESTAMP, Query.Direction.DESCENDING)
            .limit(30)

        return try {
            val messages = ArrayList<Message>()

            query.get().await().forEach { message ->
                message?.toMessage()?.let {
                    messages.add(it)
                }
            }

            messages.ifEmpty { null }
        } catch (e: Exception) {
            Log.e(TAG, "getMessagesBefore: ", e)
            null
        }
    }
}