package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreHomeworkContract
import java.util.*

data class Homework(
    var courseId: String,
    val lessonId: String? = null,
    var studentId: String,
    var studentFullName: String,
    val tutorId: String,
    val tutorFullName: String,
    val topic: String? = null,
    var subject: String,
    val creationDate: Date? = null,
    var dueDate: Date? = null,
    val status: String = FirestoreHomeworkContract.STATUS_ASSIGNED,
    var description: String? = null
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

        fun initHomework(course: Course): Homework {
            return Homework(
                courseId = course.courseId!!,
                studentId = course.studentId!!,
                studentFullName = course.studentFullName!!,
                tutorId = course.tutorId,
                tutorFullName = course.tutorFullName!!,
                subject = course.subject!!
            )
        }

        private const val TAG = "Homework"
    }
}
