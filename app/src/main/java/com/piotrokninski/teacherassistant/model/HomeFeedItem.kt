package com.piotrokninski.teacherassistant.model

import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation

sealed class HomeFeedItem {
    abstract val id: String

    data class Invitation(val friendInvitation: FriendInvitation): HomeFeedItem() {
        override val id = friendInvitation.invitingUserId

        fun getInvitationType(): String? {
            return when (friendInvitation.invitationType) {
                FirestoreFriendInvitationContract.TYPE_STUDENT -> "Zaprasza cię do grona uczniów"

                FirestoreFriendInvitationContract.TYPE_TUTOR -> "Zaprasza cię jako swojego korepetytora"

                FirestoreFriendInvitationContract.TYPE_FRIEND -> "Zaprasza cię do grona znajomych"

                else -> null
            }
        }
    }
}
