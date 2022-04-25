package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreCourseContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.util.WeekDate
import java.io.Serializable

data class Course(
    var courseId: String? = null,
    var studentId: String? = null,
    var tutorId: String,
    var studentFullName: String? = null,
    var tutorFullName: String? = null,
    val status: String = FirestoreCourseContract.STATUS_PENDING,
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
                val courseId = map[FirestoreCourseContract.COURSE_ID] as String?
                val studentId = map[FirestoreCourseContract.STUDENT_ID] as String
                val tutorId = map[FirestoreCourseContract.TUTOR_ID] as String
                val studentFullName = map[FirestoreCourseContract.STUDENT_FULL_NAME] as String
                val tutorFullName = map[FirestoreCourseContract.TUTOR_FULL_NAME] as String
                val status = map[FirestoreCourseContract.STATUS] as String
                val type = map[FirestoreCourseContract.COURSE_TYPE] as String
                val subject = map[FirestoreCourseContract.SUBJECT] as String
                val meetingDatesMap =
                    map[FirestoreCourseContract.MEETING_DATES] as ArrayList<Map<String, Any>>

                val meetingDates = ArrayList<WeekDate>()

                meetingDatesMap.forEach {
                    val weekDate = WeekDate.toWeekDate(it)
                    if (weekDate != null) meetingDates.add(weekDate)
                }

                Course(
                    courseId,
                    studentId,
                    tutorId,
                    studentFullName,
                    tutorFullName,
                    status,
                    type,
                    subject,
                    meetingDates
                )
            } catch (e: Exception) {
                Log.e(TAG, "toCourse: ", e)
                null
            }
        }

        fun createCourseWithInvitation(friendInvitation: FriendInvitation): Course {
            return when (friendInvitation.invitationType) {
                FirestoreFriendInvitationContract.TYPE_STUDENT -> {
                    Course(
                        studentId = friendInvitation.invitedUserId,
                        studentFullName = friendInvitation.invitedUserFullName,
                        tutorId = friendInvitation.invitingUserId,
                        tutorFullName = friendInvitation.invitingUserFullName
                    )
                }

                FirestoreFriendInvitationContract.TYPE_TUTOR -> {
                    Course(
                        studentId = friendInvitation.invitingUserId,
                        studentFullName = friendInvitation.invitingUserFullName,
                        tutorId = friendInvitation.invitedUserId,
                        tutorFullName = friendInvitation.invitedUserFullName
                    )
                }

                else -> {
                    throw IllegalArgumentException()
                }
            }
        }

        private const val TAG = "Course"
    }
}
