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
                    getString(COURSE_ID)!!,
                    getString(MEETING_ID)!!,
                    getString(NOTE_ID),
                    getString(STUDENT_ID)!!,
                    getString(TUTOR_ID)!!,
                    getString(STUDENT_FULL_NAME)!!,
                    getString(TUTOR_FULL_NAME)!!,
                    getString(TOPIC),
                    getString(SUBJECT)!!,
                )
            } catch (e: Exception) {
                Log.e(TAG, "toNote: ", e)
                null
            }
        }

        //Contract
        const val COLLECTION_NAME = "lessons"

        const val COURSE_ID = "courseId"
        private const val MEETING_ID = "meetingId"
        private const val NOTE_ID = "noteId"
        private const val STUDENT_ID = "studentId"
        private const val TUTOR_ID = "tutorId"
        private const val STUDENT_FULL_NAME = "studentFullName"
        private const val TUTOR_FULL_NAME = "tutorFullName"
        private const val TOPIC = "topic"
        private const val SUBJECT = "subject"

        private const val TAG = "Note"
    }
}
