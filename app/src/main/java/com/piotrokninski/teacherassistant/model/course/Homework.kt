package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreHomeworkContract
import java.util.*

data class Homework(
    val courseId: String,
    val lessonId: String?,
    val studentId: String,
    val studentFullName: String,
    val tutorId: String,
    val tutorFullName: String,
    val topic: String,
    val subject: String,
    val creationDate: Date,
    val dueDate: Date,
    val status: String,
    val description: String
) {
    companion object {

        fun DocumentSnapshot.toHomework(): Homework? {
            return try {

                val courseId = getString(FirestoreHomeworkContract.COURSE_ID)!!
                val lessonId = getString(FirestoreHomeworkContract.LESSON_ID)
                val studentId = getString(FirestoreHomeworkContract.STUDENT_ID)!!
                val studentFullName = getString(FirestoreHomeworkContract.STUDENT_FULL_NAME)!!
                val tutorId = getString(FirestoreHomeworkContract.TUTOR_ID)!!
                val tutorFullName = getString(FirestoreHomeworkContract.TUTOR_FULL_NAME)!!
                val topic = getString(FirestoreHomeworkContract.TOPIC)!!
                val subject = getString(FirestoreHomeworkContract.SUBJECT)!!
                val creationDate = getDate(FirestoreHomeworkContract.CREATION_DATE)!!
                val dueDate = getDate(FirestoreHomeworkContract.DUE_DATE)!!
                val status = getString(FirestoreHomeworkContract.STATUS)!!
                val description = getString(FirestoreHomeworkContract.DESCRIPTION)!!

                Homework(
                    courseId,
                    lessonId,
                    studentId,
                    studentFullName,
                    tutorId,
                    tutorFullName,
                    topic,
                    subject,
                    creationDate,
                    dueDate,
                    status,
                    description
                )

            } catch (e: Exception) {
                Log.e(TAG, "toHomework: ", e)
                null
            }
        }

        private const val TAG = "Homework"
    }
}
