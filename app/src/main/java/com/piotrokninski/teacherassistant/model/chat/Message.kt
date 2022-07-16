package com.piotrokninski.teacherassistant.model.chat

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import java.util.*

data class Message(
    var id: String?,
    val text: String,
    val senderId: String,
    val senderFullName: String,
    val itemId: String?,
    val timestamp: Timestamp
) {

    fun toSnapshot(): String {
        val textSnap = if (text.length < 23) {
            text
        } else {
            "${text.slice(0..22).trim()}..."
        }
        val calendar = Calendar.getInstance()
        calendar.time = timestamp.toDate()

        var hour = "${calendar.get(Calendar.HOUR_OF_DAY)}"
        var minute = "${calendar.get(Calendar.MINUTE)}"

        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) hour = "0$hour"
        if (calendar.get(Calendar.MINUTE) < 10) minute = "0$minute"

        return "$textSnap ⋅ $hour:$minute"
    }

    companion object {

        fun DocumentSnapshot.toMessage() : Message? {
            return try {
                toMessage(data!!)
            } catch (e: Exception) {
                Log.e(TAG, "toMessage: ", e)
                null
            }
        }

        fun toMessage(map: Map<String, Any>): Message? {
            return try {
                Message(
                    map[ID] as String?,
                    map[TEXT] as String,
                    map[SENDER_ID] as String,
                    map[SENDER_FULL_NAME] as String,
                    map[ITEM_ID] as String?,
                    map[TIMESTAMP] as Timestamp
                )
            } catch (e: Exception) {
                Log.e(TAG, "toCourse: ", e)
                null
            }
        }

        // Contract
        const val COLLECTION_NAME = "messages"

        const val ID = "id"
        const val TEXT = "text"
        const val SENDER_ID = "senderId"
        const val SENDER_FULL_NAME = "senderFullName"
        const val ITEM_ID = "itemId"
        const val TIMESTAMP = "timestamp"

        private const val TAG = "Message"
    }
}
