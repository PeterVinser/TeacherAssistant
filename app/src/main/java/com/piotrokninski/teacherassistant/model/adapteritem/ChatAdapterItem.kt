package com.piotrokninski.teacherassistant.model.adapteritem

import com.google.firebase.Timestamp
import com.piotrokninski.teacherassistant.model.chat.Chat
import com.piotrokninski.teacherassistant.model.chat.Message

sealed class ChatAdapterItem {

    abstract val id: String
    abstract val timeStamp: Timestamp

    data class ReceivedMessage(val message: Message) : ChatAdapterItem() {
        override val id = message.timestamp.toString()
        override val timeStamp = message.timestamp
    }

    data class SentMessage(val message: Message): ChatAdapterItem() {
        override val id = message.timestamp.toString()
        override val timeStamp = message.timestamp
    }
}
