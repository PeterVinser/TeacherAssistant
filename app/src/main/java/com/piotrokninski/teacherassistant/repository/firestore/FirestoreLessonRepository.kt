package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.course.Lesson
import com.piotrokninski.teacherassistant.model.course.Lesson.Companion.toLesson
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreLessonContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreLessonSnapshotContract
import com.piotrokninski.teacherassistant.model.course.LessonSnapshot
import com.piotrokninski.teacherassistant.model.course.LessonSnapshot.Companion.toLessonSnapshot
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object FirestoreLessonRepository {
    private const val TAG = "FirestoreNoteRepository"

    fun setLesson(lesson: Lesson) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreLessonContract.COLLECTION_NAME).document()
            .set(lesson)
    }

    suspend fun addLesson(lesson: Lesson) {
        val db = FirebaseFirestore.getInstance()

        val lessonId = db.collection(FirestoreLessonContract.COLLECTION_NAME).add(lesson).await().id

//        val lessonSnapshot = LessonSnapshot(lesson.courseId, lessonId, lesson.topic)

//        db.collection(FirestoreLessonSnapshotContract.COLLECTION_NAME).add(lessonSnapshot)
    }

    suspend fun getCourseLessons(courseId: String): ArrayList<Lesson>? {
        val db = FirebaseFirestore.getInstance()

        val lessonsRef = db.collection(FirestoreLessonContract.COLLECTION_NAME)

        val query = lessonsRef.whereEqualTo(FirestoreLessonContract.COURSE_ID, courseId)
            .whereEqualTo(FirestoreLessonContract.COMPLETED, true)

        return try {

            val lessons = ArrayList<Lesson>()

            query.get().await().forEach { lesson ->
                lesson?.toLesson()?.let { lessons.add(it) }
            }

            if (lessons.isEmpty()) {
                null
            } else {
                lessons
            }

        } catch (e: Exception) {
            Log.e(TAG, "getCourseNotes: ", e)
            null
        }
    }

    suspend fun getCourseLessonSnapshots(courseId: String): ArrayList<LessonSnapshot>? {
        val db = FirebaseFirestore.getInstance()

        val lessonSnapshotsRef = db.collection(FirestoreLessonSnapshotContract.COLLECTION_NAME)

        val query = lessonSnapshotsRef.whereEqualTo(FirestoreLessonSnapshotContract.COURSE_ID, courseId)

        return try {

            val lessonSnapshots = ArrayList<LessonSnapshot>()

            query.get().await().forEach { snapshot ->
                snapshot?.toLessonSnapshot()?.let { lessonSnapshots.add(it) }
            }

            if (lessonSnapshots.isEmpty()) {
                null
            } else {
                lessonSnapshots
            }

        } catch (e: Exception) {
            Log.e(TAG, "getCourseLessonSnapshots: ", e)
            null
        }
    }
}