package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.chat.Chat
import com.piotrokninski.teacherassistant.model.chat.Chat.Companion.toChat
import kotlinx.coroutines.tasks.await

object FirestoreChatRepository {
    private const val TAG = "FirestoreChatRepository"

    suspend fun getChat(chatId: String): Chat? {
        val db = FirebaseFirestore.getInstance()

        val ref = db.collection(Chat.COLLECTION_NAME).document(chatId)

        return try {
             ref.get().await()?.toChat()
        } catch (e: Exception) {
            Log.e(TAG, "getChat: ", e)
            null
        }
    }
}