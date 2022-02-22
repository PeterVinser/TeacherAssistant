package com.piotrokninski.teacherassistant.model.meeting

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreMeetingContract
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

data class Meeting(
    val courseId: String? = null,
    val lessonId: String? = null,
    val studentId: String,
    val studentFullName: String,
    val tutorId: String,
    val tutorFullName: String,
    val subject: String,
    val date: Date,
    val status: String,
    val description: String,
    val meetingDates: ArrayList<String>? = null
) {
    companion object {
        fun DocumentSnapshot.toMeeting(): Meeting? {
            return try {
                val courseId = getString(FirestoreMeetingContract.COURSE_ID)
                val lessonId = getString(FirestoreMeetingContract.LESSON_ID)
                val studentId = getString(FirestoreMeetingContract.STUDENT_ID)!!
                val studentFullName = getString(FirestoreMeetingContract.STUDENT_FULL_NAME)!!
                val tutorId = getString(FirestoreMeetingContract.TUTOR_ID)!!
                val tutorFullName = getString(FirestoreMeetingContract.TUTOR_FULL_NAME)!!
                val subject = getString(FirestoreMeetingContract.SUBJECT)!!
                val date = getDate(FirestoreMeetingContract.DATE)!!
                val status = getString(FirestoreMeetingContract.STATUS)!!
                val description = getString(FirestoreMeetingContract.DESCRIPTION)!!
                val meetingDates = get(FirestoreMeetingContract.MEETING_DATES) as ArrayList<String>

                return Meeting(
                    courseId,
                    lessonId,
                    studentId,
                    studentFullName,
                    tutorId,
                    tutorFullName,
                    subject,
                    date,
                    status,
                    description,
                    meetingDates
                )
            } catch (e: Exception) {
                Log.e(TAG, "toMeeting: ", e)
                null
            }
        }

        private const val TAG = "Meeting"
    }
}
