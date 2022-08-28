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
    val attachment: Attachment?,
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
                val attachment = (map[Contract.ATTACHMENT] as Map<String, Any>?)?.let {
                    Attachment.toAttachment(it)
                }

                Message(
                    map[Contract.ID] as String?,
                    map[Contract.TEXT] as String,
                    map[Contract.SENDER_ID] as String,
                    map[Contract.SENDER_FULL_NAME] as String,
                    attachment,
                    map[Contract.TIMESTAMP] as Timestamp
                )
            } catch (e: Exception) {
                Log.e(TAG, "toCourse: ", e)
                null
            }
        }

        private const val TAG = "Message"
    }

    object Contract {
        const val COLLECTION_NAME = "messages"

        const val ID = "id"
        const val TEXT = "text"
        const val SENDER_ID = "senderId"
        const val SENDER_FULL_NAME = "senderFullName"
        const val TIMESTAMP = "timestamp"
        const val ATTACHMENT = "attachment"
    }
}
