package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreLessonContract
import java.lang.Exception

data class Lesson(
    val courseId: String,
    val studentId: String,
    val tutorId: String,
    val studentFullName: String,
    val tutorFullName: String,
    val topic: String?,
    val subject: String,
    val date: String,
    val note: String?,
    val completed: Boolean = false
) {

    companion object {
        fun DocumentSnapshot.toLesson(): Lesson? {
            return try {

                val courseId = getString(FirestoreLessonContract.COURSE_ID)!!
                val studentId = getString(FirestoreLessonContract.STUDENT_ID)!!
                val tutorId = getString(FirestoreLessonContract.TUTOR_ID)!!
                val studentFullName = getString(FirestoreLessonContract.STUDENT_FULL_NAME)!!
                val tutorFullName = getString(FirestoreLessonContract.TUTOR_FULL_NAME)!!
                val topic = getString(FirestoreLessonContract.TOPIC)
                val subject = getString(FirestoreLessonContract.SUBJECT)!!
                val date = getString(FirestoreLessonContract.DATE)!!
                val note = getString(FirestoreLessonContract.NOTE)
                val completed = getBoolean(FirestoreLessonContract.COMPLETED)!!

                Lesson(
                    courseId,
                    studentId,
                    tutorId,
                    studentFullName,
                    tutorFullName,
                    topic,
                    subject,
                    date,
                    note,
                    completed
                )

            } catch (e: Exception) {
                Log.e(TAG, "toNote: ", e)
                null
            }
        }

        private const val TAG = "Note"
    }
}
