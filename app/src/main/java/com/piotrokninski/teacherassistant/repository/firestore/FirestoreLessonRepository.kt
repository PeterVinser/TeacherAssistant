package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.course.Lesson
import com.piotrokninski.teacherassistant.model.course.Lesson.Companion.toLesson
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreLessonContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreLessonSnapshotContract
import com.piotrokninski.teacherassistant.model.course.LessonSnapshot
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

        val lessonSnapshot = LessonSnapshot(lesson.courseId, lessonId, lesson.topic)

        db.collection(FirestoreLessonSnapshotContract.COLLECTION_NAME).add(lessonSnapshot)
    }

    suspend fun getCourseLessons(courseId: String): ArrayList<Lesson>? {
        val db = FirebaseFirestore.getInstance()

        val notesRef = db.collection(FirestoreLessonContract.COLLECTION_NAME)

        val query = notesRef.whereEqualTo(FirestoreLessonContract.COURSE_ID,courseId)

        return try {

            val notes = ArrayList<Lesson>()

            query.get().await().forEach { note ->
                note?.toLesson()?.let { notes.add(it) }
            }

            if (notes.isEmpty()) {
                null
            } else {
                notes
            }

        } catch (e: Exception) {
            Log.e(TAG, "getCourseNotes: ", e)
            null
        }
    }
}