package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

data class Homework(
    var courseId: String,
    var lessonId: String? = null,
    var studentId: String,
    var studentFullName: String,
    val tutorId: String,
    val tutorFullName: String,
    var topic: String? = null,
    var subject: String,
    var creationDate: Date? = null,
    var dueDate: Date? = null,
    val reminderDate: Date? = null,
    var reminded: Boolean = false,
    val status: String = STATUS_ASSIGNED,
    var description: String? = null
) {
    companion object {

        fun DocumentSnapshot.toHomework(): Homework? {
            return try {

                Homework(
                    getString(COURSE_ID)!!,
                    getString(LESSON_ID),
                    getString(STUDENT_FULL_NAME)!!,
                    getString(STUDENT_FULL_NAME)!!,
                    getString(TUTOR_ID)!!,
                    getString(TUTOR_FULL_NAME)!!,
                    getString(TOPIC),
                    getString(SUBJECT)!!,
                    getDate(CREATION_DATE),
                    getDate(DUE_DATE)!!,
                    getDate(REMINDER_DATE)!!,
                    getBoolean(REMINDED)!!,
                    getString(STATUS)!!,
                    getString(DESCRIPTION)!!
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

        //Contract
        const val COLLECTION_NAME = "homework"

        const val COURSE_ID = "courseId"
        private const val LESSON_ID = "lessonId"
        const val STUDENT_ID = "studentId"
        private const val STUDENT_FULL_NAME = "studentFullName"
        const val TUTOR_ID = "tutorId"
        private const val TUTOR_FULL_NAME = "tutorFullName"
        private const val TOPIC = "topic"
        private const val SUBJECT = "subject"
        private const val CREATION_DATE = "creationDate"
        private const val DUE_DATE = "dueDate"
        private const val REMINDER_DATE = "reminderDate"
        private const val REMINDED = "reminded"
        const val STATUS = "status"
        private const val DESCRIPTION = "description"

        const val STATUS_ASSIGNED = "assigned"
        const val STATUS_COMPLETED = "completed"
        const val STATUS_UNDELIVERED = "undelivered"

        private const val TAG = "Homework"
    }
}
