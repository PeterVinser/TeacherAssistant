package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.util.WeekDate
import java.io.Serializable

data class Course(
    var courseId: String? = null,
    var studentId: String? = null,
    var tutorId: String,
    var studentFullName: String? = null,
    var tutorFullName: String? = null,
    val status: String = STATUS_PENDING,
    var type: String? = null,
    var subject: String? = null,
    var meetingDates: ArrayList<WeekDate>? = null
) : Serializable {

    companion object {

        fun DocumentSnapshot.toCourse(): Course? {
            return try {
                toCourse(data!!)
            } catch (e: Exception) {
                Log.e(TAG, "toCourse: ", e)
                null
            }
        }

        fun toCourse(map: Map<String, Any>): Course? {
            return try {
                val meetingDates = ArrayList<WeekDate>()

                (map[MEETING_DATES] as ArrayList<Map<String, Any>>).forEach {
                    WeekDate.toWeekDate(it)?.let { weekDate -> meetingDates.add(weekDate) }
                }

                Course(
                    map[COURSE_ID] as String?,
                    map[STUDENT_ID] as String,
                    map[TUTOR_ID] as String,
                    map[STUDENT_FULL_NAME] as String,
                    map[TUTOR_FULL_NAME] as String,
                    map[STATUS] as String,
                    map[TYPE] as String,
                    map[SUBJECT] as String,
                    meetingDates
                )
            } catch (e: Exception) {
                Log.e(TAG, "toCourse: ", e)
                null
            }
        }

        fun createCourseWithInvitation(friendInvitation: FriendInvitation): Course {
            return when (friendInvitation.invitationType) {
                FriendInvitation.TYPE_STUDENT -> {
                    Course(
                        studentId = friendInvitation.invitedUserId,
                        studentFullName = friendInvitation.invitedUserFullName,
                        tutorId = friendInvitation.invitingUserId,
                        tutorFullName = friendInvitation.invitingUserFullName,
                        status = STATUS_ATTACHED
                    )
                }

                FriendInvitation.TYPE_TUTOR -> {
                    Course(
                        studentId = friendInvitation.invitingUserId,
                        studentFullName = friendInvitation.invitingUserFullName,
                        tutorId = friendInvitation.invitedUserId,
                        tutorFullName = friendInvitation.invitedUserFullName,
                        status = STATUS_ATTACHED
                    )
                }

                else -> {
                    throw IllegalArgumentException()
                }
            }
        }

        //Contract
        const val COLLECTION_NAME = "courses"

        const val COURSE_ID = "courseId"
        const val STUDENT_ID = "studentId"
        const val TUTOR_ID = "tutorId"
        private const val STUDENT_FULL_NAME = "studentFullName"
        private const val TUTOR_FULL_NAME = "tutorFullName"
        const val STATUS = "status"
        private const val TYPE = "type"
        private const val SUBJECT = "subject"
        private const val MEETING_DATES = "meetingDates"

        const val STATUS_PENDING = "pending"
        const val STATUS_APPROVED = "approved"
        private const val STATUS_ATTACHED = "attached" //when attached to invitation

        private const val TAG = "Course"
    }
}
