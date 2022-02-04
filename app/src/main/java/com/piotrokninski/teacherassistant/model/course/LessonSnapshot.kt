package com.piotrokninski.teacherassistant.model.course

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreLessonSnapshotContract
import java.lang.Exception

data class LessonSnapshot(
    val courseId: String,
    val lessonId: String,
    val topic: String
) {
    companion object {
        fun DocumentSnapshot.toLessonSnapshot(): LessonSnapshot? {
            return try {

                val courseId = getString(FirestoreLessonSnapshotContract.COURSE_ID)!!
                val lessonId = getString(FirestoreLessonSnapshotContract.LESSON_ID)!!
                val topic = getString(FirestoreLessonSnapshotContract.TOPIC)!!

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
}
