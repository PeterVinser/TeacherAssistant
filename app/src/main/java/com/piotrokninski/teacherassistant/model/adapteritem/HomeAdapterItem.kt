package com.piotrokninski.teacherassistant.model.adapteritem

import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.model.meeting.MeetingInvitation

sealed class HomeAdapterItem {
    abstract val id: String

    data class HeaderItem(val titleId: Int): HomeAdapterItem() {
        override val id = titleId.toString()
    }

    data class FriendInvitationItem(val friendInvitation: FriendInvitation): HomeAdapterItem() {
        override val id = friendInvitation.invitationId ?: friendInvitation.invitedUserId

        fun getInvitationType(): String? {
            return when (friendInvitation.invitationType) {
                FirestoreFriendInvitationContract.TYPE_STUDENT -> "Zaprasza cię do grona uczniów"

                FirestoreFriendInvitationContract.TYPE_TUTOR -> "Zaprasza cię jako swojego korepetytora"

                FirestoreFriendInvitationContract.TYPE_FRIEND -> "Zaprasza cię do grona znajomych"

                else -> null
            }
        }
    }

    data class CourseItem(val course: Course): HomeAdapterItem() {
        override val id = course.courseId!!
    }

    data class HomeworkItem(val homework: Homework): HomeAdapterItem() {
        override val id = homework.toString()
    }

    data class MeetingInvitationItem(val meetingInvitation: MeetingInvitation): HomeAdapterItem() {
        override val id = meetingInvitation.invitingUserId
    }
}
