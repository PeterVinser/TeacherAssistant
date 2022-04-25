package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.messaging.FirebaseMessaging
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.course.Course.Companion.toCourse
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreCourseContract
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object FirestoreCourseRepository {
    private const val TAG = "FirestoreCourseReposito"

    fun addCourse(course: Course) {
        val db = FirebaseFirestore.getInstance()

        val document = db.collection(FirestoreCourseContract.COLLECTION_NAME).document()
        course.courseId = document.id

        document.set(course)
    }

    fun updateCourse(courseId: String, field: String, value: Any) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreCourseContract.COLLECTION_NAME).document(courseId)
            .update(field, value)
    }

    suspend fun getStudiedCourses(userId: String, status: String): ArrayList<Course>? {
        val db = FirebaseFirestore.getInstance()

        val coursesRef = db.collection(FirestoreCourseContract.COLLECTION_NAME)

        val query = coursesRef.whereEqualTo(FirestoreCourseContract.STUDENT_ID, userId)
            .whereEqualTo(FirestoreCourseContract.STATUS, status)

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

    suspend fun getTaughtCourses(userId: String, status: String): ArrayList<Course>? {
        val db = FirebaseFirestore.getInstance()

        val coursesRef = db.collection(FirestoreCourseContract.COLLECTION_NAME)

        val query = coursesRef.whereEqualTo(FirestoreCourseContract.TUTOR_ID, userId)
            .whereEqualTo(FirestoreCourseContract.STATUS, status)

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
            db.collection(FirestoreCourseContract.COLLECTION_NAME).document(courseId)
                .delete()
        } catch (e: Exception) {
            Log.e(TAG, "deleteCourse: ", e)
        }
    }
}