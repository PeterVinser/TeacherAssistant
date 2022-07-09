package com.piotrokninski.teacherassistant.model.adapteritem

import com.piotrokninski.teacherassistant.model.chat.Message
import com.piotrokninski.teacherassistant.model.friend.Friend

sealed class ContactAdapterItem {

    abstract val id: String

    data class FriendAdapterItem(val friend: Friend, val latestMessage: Message?, val read: Boolean) : ContactAdapterItem() {
        override val id = friend.userId
    }

    data class HeaderAdapterItem(val titleId: Int): ContactAdapterItem() {
        override val id = titleId.toString()
    }
}
