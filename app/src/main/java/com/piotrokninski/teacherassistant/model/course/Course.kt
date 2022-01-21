package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreCourseContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendInvitationContract
import java.io.Serializable

data class Course(
    var courseId: String,
    val studentId: String,
    val tutorId: String,
    val studentFullName: String,
    val tutorFullName: String,
    val status: String,
    var type: String?,
    var subject: String?,
    var meetingsDates: ArrayList<String>?
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
                val meetingsDates = get(FirestoreCourseContract.MEETINGS_DATES) as ArrayList<String>

                Course(
                    courseId,
                    studentId,
                    tutorId,
                    studentFullName,
                    tutorFullName,
                    status,
                    type,
                    subject,
                    meetingsDates
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
                        "",
                        friendInvitation.invitedUserId,
                        friendInvitation.invitingUserId,
                        friendInvitation.invitedUserFullName,
                        friendInvitation.invitingUserFullName,
                        FirestoreCourseContract.STATUS_PENDING,
                        null,
                        null,
                        null
                    )
                }

                FirestoreFriendInvitationContract.TYPE_TUTOR -> {
                    Course(
                        "",
                        friendInvitation.invitingUserId,
                        friendInvitation.invitedUserId,
                        friendInvitation.invitingUserFullName,
                        friendInvitation.invitedUserFullName,
                        FirestoreCourseContract.STATUS_PENDING,
                        null,
                        null,
                        null
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
