package com.piotrokninski.teacherassistant.model.adapteritem

import com.piotrokninski.teacherassistant.model.friend.FriendInvitation

sealed class ContactAdapterItem {

    abstract val id: String

    data class FriendAdapterItem(val userId: String, val fullName: String) : ContactAdapterItem() {
        override val id = userId
    }

    data class FriendInvitationAdapterItem(val sentReceived: String, val friendInvitation: FriendInvitation) : ContactAdapterItem() {
        override val id = friendInvitation.invitingUserId
    }

    data class HeaderAdapterItem(val titleId: Int): ContactAdapterItem() {
        override val id = titleId.toString()
    }
}
