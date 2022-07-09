package com.piotrokninski.teacherassistant.model.adapteritem

import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.model.meeting.MeetingInvitation

sealed class HomeAdapterItem {
    abstract val id: String

    data class HeaderItem(val titleId: Int): HomeAdapterItem() {
        override val id = titleId.toString()
    }

    data class InvitationItem(val invitation: Invitation, val received: Boolean): HomeAdapterItem() {
        override val id = invitation.id ?: invitation.invitingUserId

        fun getInvitationDescription(): String? {

            val friendStarter = if (received) {
                "Zaprasza cię"
            } else {
                "Zapraszony"
            }

            val eventStarter = if (received) {
                "Zaprasza"
            } else {
                "Zapraszony"
            }

            return when (invitation.type) {
                Invitation.Contract.TYPE_FRIENDSHIP -> {
                    when (invitation.invitedAs) {
                        Invitation.Contract.STUDENT -> "$friendStarter do grona uczniów"

                        Invitation.Contract.TUTOR -> {
                            if (received) {
                                "Zaprasza cię jako swojego korepetytora"
                            } else {
                                "Zaproszony jako korepetytor"
                            }
                        }

                        Invitation.Contract.FRIEND -> "$friendStarter do grona znajomych"

                        else -> null
                    }
                }

                Invitation.Contract.TYPE_COURSE -> "$eventStarter na nowe zajęcia"

                Invitation.Contract.TYPE_MEETING -> "$eventStarter na nowe spotkanie"

                else -> null
            }
        }
    }

    data class HomeworkItem(val homework: Homework): HomeAdapterItem() {
        override val id = homework.toString()
    }
}
