package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.piotrokninski.teacherassistant.model.chat.Chat
import com.piotrokninski.teacherassistant.model.chat.Chat.Companion.toChat
import kotlinx.coroutines.tasks.await

object FirestoreChatRepository {
    private const val TAG = "FirestoreChatRepository"

    fun updateChat(chatId: String, field: String, value: Any) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Chat.Contract.COLLECTION_NAME).document(chatId)
            .update(field, value)
    }

    suspend fun getChat(chatId: String): Chat? {
        val db = FirebaseFirestore.getInstance()

        val ref = db.collection(Chat.Contract.COLLECTION_NAME).document(chatId)

        return try {
             ref.get().await()?.toChat()
        } catch (e: Exception) {
            Log.e(TAG, "getChat: ", e)
            null
        }
    }

    suspend fun getChats(userId: String, type: String): List<Chat>? {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection(Chat.Contract.COLLECTION_NAME)
            .whereGreaterThan("${Chat.Contract.USERS}.${userId}", type)

        return try {
            val chats = ArrayList<Chat>()

            query.get().await().forEach { chat ->
                chat.toChat()?.let { chats.add(it) }
            }

            chats.ifEmpty { null }
        } catch (e: Exception) {
            Log.e(TAG, "getChats: ", e)
            null
        }
    }
}