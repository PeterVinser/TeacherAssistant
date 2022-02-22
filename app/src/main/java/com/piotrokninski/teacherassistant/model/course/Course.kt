package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreCourseContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
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
    var meetingDates: ArrayList<String>? = null
) : Serializable {

    companion object {

        fun DocumentSnapshot.toCourse(): Course? {
            return try {
                val courseId = getString(FirestoreCourseContract.COURSE_ID)!!
                val studentId = getString(FirestoreCourseContract.STUDENT_ID)!!
                val tutorId = getString(FirestoreCourseContract.TUTOR_ID)!!
                val studentFullName = getString(FirestoreCourseContract.STUDENT_FULL_NAME)!!
                val tutorFullName = getString(FirestoreCourseContract.TUTOR_FULL_NAME)!!
                val status = getString(FirestoreCourseContract.STATUS)!!
                val type = getString(FirestoreCourseContract.COURSE_TYPE)
                val subject = getString(FirestoreCourseContract.SUBJECT)
                val meetingDates = get(FirestoreCourseContract.MEETING_DATES) as ArrayList<String>

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
