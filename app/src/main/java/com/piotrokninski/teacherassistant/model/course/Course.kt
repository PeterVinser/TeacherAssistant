package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.piotrokninski.teacherassistant.model.Invitation
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.util.WeekDate
import java.io.Serializable

data class Course(
    var courseId: String? = null,
    var studentId: String? = null,
    var tutorId: String,
    var studentFullName: String? = null,
    var tutorFullName: String? = null,
    var type: String? = null,
    var subject: String? = null,
    var weekDates: ArrayList<WeekDate>? = null
) : Serializable {

    @get:Exclude
    val isComplete: Boolean
        get() = (!studentId.isNullOrEmpty() && weekDates != null
                && !subject.isNullOrEmpty() && !type.isNullOrEmpty())


    companion object {

        fun DocumentSnapshot.toCourse(): Course?  =
            toCourse(data!!)

        fun toCourse(map: Map<String, Any>): Course? {
            return try {
                val meetingDates = ArrayList<WeekDate>()

                (map[WEEK_DATES] as ArrayList<Map<String, Any>>).forEach {
                    WeekDate.toWeekDate(it)?.let { weekDate -> meetingDates.add(weekDate) }
                }

                Course(
                    map[COURSE_ID] as String?,
                    map[STUDENT_ID] as String,
                    map[TUTOR_ID] as String,
                    map[STUDENT_FULL_NAME] as String,
                    map[TUTOR_FULL_NAME] as String,
                    map[TYPE] as String,
                    map[SUBJECT] as String,
                    meetingDates
                )
            } catch (e: Exception) {
                Log.e(TAG, "toCourse: ", e)
                null
            }
        }

        fun createCourseWithInvitation(invitation: Invitation): Course {
            return when (invitation.invitedAs) {
                FriendInvitation.TYPE_STUDENT -> {
                    Course(
                        studentId = invitation.invitedUserId,
                        studentFullName = invitation.invitedUserFullName,
                        tutorId = invitation.invitingUserId,
                        tutorFullName = invitation.invitingUserFullName
                    )
                }

                FriendInvitation.TYPE_TUTOR -> {
                    Course(
                        studentId = invitation.invitingUserId,
                        studentFullName = invitation.invitingUserFullName,
                        tutorId = invitation.invitedUserId!!,
                        tutorFullName = invitation.invitedUserFullName
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
        private const val WEEK_DATES = "weekDates"

        const val STATUS_PENDING = "pending"
        const val STATUS_APPROVED = "approved"

        private const val TAG = "Course"
    }
}
