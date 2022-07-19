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
    val status: String = Contract.STATUS_ASSIGNED,
    var description: String? = null
) {
    companion object {

        fun DocumentSnapshot.toHomework(): Homework? {
            return try {

                Homework(
                    getString(Contract.COURSE_ID)!!,
                    getString(Contract.LESSON_ID),
                    getString(Contract.STUDENT_ID)!!,
                    getString(Contract.STUDENT_FULL_NAME)!!,
                    getString(Contract.TUTOR_ID)!!,
                    getString(Contract.TUTOR_FULL_NAME)!!,
                    getString(Contract.TOPIC),
                    getString(Contract.SUBJECT)!!,
                    getDate(Contract.CREATION_DATE),
                    getDate(Contract.DUE_DATE)!!,
                    getDate(Contract.REMINDER_DATE)!!,
                    getBoolean(Contract.REMINDED)!!,
                    getString(Contract.STATUS)!!,
                    getString(Contract.DESCRIPTION)!!
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
    
    object Contract {

        const val COLLECTION_NAME = "homework"

        const val COURSE_ID = "courseId"
        const val LESSON_ID = "lessonId"
        const val STUDENT_ID = "studentId"
        const val STUDENT_FULL_NAME = "studentFullName"
        const val TUTOR_ID = "tutorId"
        const val TUTOR_FULL_NAME = "tutorFullName"
        const val TOPIC = "topic"
        const val SUBJECT = "subject"
        const val CREATION_DATE = "creationDate"
        const val DUE_DATE = "dueDate"
        const val REMINDER_DATE = "reminderDate"
        const val REMINDED = "reminded"
        const val STATUS = "status"
        const val DESCRIPTION = "description"

        const val STATUS_ASSIGNED = "assigned"
        const val STATUS_COMPLETED = "completed"
        const val STATUS_UNDELIVERED = "undelivered"
    }
}
