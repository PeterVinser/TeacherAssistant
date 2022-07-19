package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Exception

data class Lesson(
    val courseId: String,
    val meetingId: String,
    val noteId: String?,
    val studentId: String,
    val tutorId: String,
    val studentFullName: String,
    val tutorFullName: String,
    val topic: String?,
    val subject: String
) {

    companion object {
        fun DocumentSnapshot.toLesson(): Lesson? {
            return try {
                Lesson(
                    getString(Contract.COURSE_ID)!!,
                    getString(Contract.MEETING_ID)!!,
                    getString(Contract.NOTE_ID),
                    getString(Contract.STUDENT_ID)!!,
                    getString(Contract.TUTOR_ID)!!,
                    getString(Contract.STUDENT_FULL_NAME)!!,
                    getString(Contract.TUTOR_FULL_NAME)!!,
                    getString(Contract.TOPIC),
                    getString(Contract.SUBJECT)!!,
                )
            } catch (e: Exception) {
                Log.e(TAG, "toNote: ", e)
                null
            }
        }

        private const val TAG = "Note"
    }
    
    object Contract {
        const val COLLECTION_NAME = "lessons"

        const val COURSE_ID = "courseId"
        const val MEETING_ID = "meetingId"
        const val NOTE_ID = "noteId"
        const val STUDENT_ID = "studentId"
        const val TUTOR_ID = "tutorId"
        const val STUDENT_FULL_NAME = "studentFullName"
        const val TUTOR_FULL_NAME = "tutorFullName"
        const val TOPIC = "topic"
        const val SUBJECT = "subject"
    }
}
