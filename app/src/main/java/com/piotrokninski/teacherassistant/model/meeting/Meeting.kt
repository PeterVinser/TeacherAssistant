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
    val completed: Boolean,
    val description: String,
    val durationHours: Int?,
    val durationMinutes: Int?
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
                val completed = getBoolean(FirestoreMeetingContract.COMPLETED)!!
                val description = getString(FirestoreMeetingContract.DESCRIPTION)!!
                val durationHours = getLong(FirestoreMeetingContract.DURATION_HOURS)!!.toInt()
                val durationMinutes = getLong(FirestoreMeetingContract.DURATION_MINUTES)!!.toInt()

                //TODO add duration logic

                return Meeting(
                    courseId,
                    lessonId,
                    studentId,
                    studentFullName,
                    tutorId,
                    tutorFullName,
                    subject,
                    date,
                    completed,
                    description,
                    durationHours,
                    durationMinutes
                )
            } catch (e: Exception) {
                Log.e(TAG, "toMeeting: ", e)
                null
            }
        }

        private const val TAG = "Meeting"
    }
}
