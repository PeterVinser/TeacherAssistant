package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.course.Course.Companion.toCourse
import kotlinx.coroutines.tasks.await

object FirestoreCourseRepository {
    private const val TAG = "FirestoreCourseReposito"

    fun addCourse(course: Course) {
        val db = FirebaseFirestore.getInstance()

        val document = db.collection(Course.Contract.COLLECTION_NAME).document()
        course.courseId = document.id

        document.set(course)
    }

    fun updateCourse(courseId: String, field: String, value: Any) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Course.Contract.COLLECTION_NAME).document(courseId)
            .update(field, value)
    }

    suspend fun getStudiedCourses(userId: String): ArrayList<Course>? {
        val db = FirebaseFirestore.getInstance()

        val coursesRef = db.collection(Course.Contract.COLLECTION_NAME)

        val query = coursesRef.whereEqualTo(Course.Contract.STUDENT_ID, userId)

        return try {

            val studiedCourses = ArrayList<Course>()

            query.get().await().forEach { course ->
                course?.toCourse()?.let { studiedCourses.add(it) }
            }

            if (studiedCourses.isEmpty()) {
                null
            } else {
                studiedCourses
            }
        } catch (e: Exception) {
            Log.e(TAG, "getStudiedCourses: ", e)
            null
        }
    }

    suspend fun getTaughtCourses(userId: String): ArrayList<Course>? {
        val db = FirebaseFirestore.getInstance()

        val coursesRef = db.collection(Course.Contract.COLLECTION_NAME)

        val query = coursesRef.whereEqualTo(Course.Contract.TUTOR_ID, userId)

        return try {

            val taughtCourses = ArrayList<Course>()

            query.get().await().forEach { course ->
                course?.toCourse()?.let { taughtCourses.add(it) }
            }

            if (taughtCourses.isEmpty()) {
                null
            } else {
                taughtCourses
            }
        } catch (e: Exception) {
            Log.e(TAG, "getTaughtCourses: ", e)
            null
        }
    }

    fun deleteCourse(courseId: String) {
        val db = FirebaseFirestore.getInstance()

        try {
            db.collection(Course.Contract.COLLECTION_NAME).document(courseId)
                .delete()
        } catch (e: Exception) {
            Log.e(TAG, "deleteCourse: ", e)
        }
    }
}