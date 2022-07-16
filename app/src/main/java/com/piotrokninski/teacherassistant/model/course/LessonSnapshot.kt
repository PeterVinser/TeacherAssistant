package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

data class LessonSnapshot(
    val courseId: String,
    val lessonId: String,
    val topic: String
) {
    companion object {
        fun DocumentSnapshot.toLessonSnapshot(): LessonSnapshot? {
            return try {

                val courseId = getString(Contract.COURSE_ID)!!
                val lessonId = getString(Contract.LESSON_ID)!!
                val topic = getString(Contract.TOPIC)!!

                LessonSnapshot(
                    courseId,
                    lessonId,
                    topic
                )

            } catch (e: Exception) {
                Log.e(TAG, "toLessonSnapshot: ", e)
                null
            }
        }

        private const val TAG = "LessonSnapshot"
    }

    object Contract {

        const val COLLECTION_NAME = "lessonSnapshots"

        const val COURSE_ID = "courseId"

        const val LESSON_ID = "lessonId"

        const val TOPIC = "topic"
    }
}
