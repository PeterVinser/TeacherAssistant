package com.piotrokninski.teacherassistant.model.chat

import android.util.Log
import java.lang.Exception

data class Attachment(
    val itemId: String,
    val title: String?,
    val type: String
) {

    companion object {
        fun toAttachment(map: Map<String, Any>): Attachment? {
            return try {
                Attachment(
                    map[Contract.ITEM_ID] as String,
                    map[Contract.TITLE] as String?,
                    map[Contract.TYPE] as String
                )
            } catch (e: Exception) {
                Log.d(TAG, "toMessage: ", e)
                null
            }
        }

        private const val TAG = "Attachment"
    }

    object Contract {
        const val ITEM_ID = "itemId"
        const val TITLE = "title"
        const val TYPE = "type"

        const val TYPE_INVITATION = "invitation"
        const val TYPE_MEETING = "meeting"
        const val TYPE_HOMEWORK = "homework"
    }
}
