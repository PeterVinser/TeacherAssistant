package com.piotrokninski.teacherassistant.model

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.FirestoreCourseContract
import com.piotrokninski.teacherassistant.model.contract.FirestoreFriendInvitationContract

data class Course(val studentId: String,
                  val tutorId: String,
                  val studentFullName: String,
                  val tutorFullName: String,
                  var type: String?,
                  var subject: String?,
                  var meetingsDates: ArrayList<String>?) {

    companion object {

        fun DocumentSnapshot.toCourse(): Course? {
            return try {
                val studentId = getString(FirestoreCourseContract.STUDENT_ID)!!
                val tutorId = getString(FirestoreCourseContract.TUTOR_ID)!!
                val studentFullName = getString(FirestoreCourseContract.STUDENT_FULL_NAME)!!
                val tutorFullName = getString(FirestoreCourseContract.TUTOR_FULL_NAME)!!
                val type = getString(FirestoreCourseContract.COURSE_TYPE)
                val subject = getString(FirestoreCourseContract.SUBJECT)
                val meetingsDates = get(FirestoreCourseContract.MEETINGS_DATES) as ArrayList<String>

                Course(studentId, tutorId, studentFullName, tutorFullName, type, subject, meetingsDates)

            } catch (e: Exception) {
                Log.e(TAG, "toCourse: ", e)
                null
            }
        }

        fun createCourseWithInvitation(friendInvitation: FriendInvitation): Course {
            return when (friendInvitation.invitationType) {
                FirestoreFriendInvitationContract.TYPE_STUDENT -> {
                    Course(friendInvitation.invitedUserId, friendInvitation.invitingUserId,
                        friendInvitation.invitedUserFullName, friendInvitation.invitingUserFullName,
                        null, null, null)
                }

                FirestoreFriendInvitationContract.TYPE_TUTOR -> {
                    Course(friendInvitation.invitingUserId, friendInvitation.invitedUserId,
                        friendInvitation.invitingUserFullName, friendInvitation.invitedUserFullName,
                        null, null, null)
                }

                else -> {
                    throw IllegalArgumentException()
                }
            }
        }

        private const val TAG = "Course"
    }
}
