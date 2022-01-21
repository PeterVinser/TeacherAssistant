package com.piotrokninski.teacherassistant.model.adapteritem

import com.piotrokninski.teacherassistant.model.friend.FriendInvitation

sealed class ContactItem {

    abstract val id: String

    data class FriendItem(val userId: String, val fullName: String) : ContactItem() {
        override val id = userId
    }

    data class FriendInvitationItem(val sentReceived: String, val friendInvitation: FriendInvitation) : ContactItem() {
        override val id = friendInvitation.invitingUserId
    }

    data class HeaderItem(val titleId: Int): ContactItem() {
        override val id = titleId.toString()
    }
}
