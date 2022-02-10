package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.course.Course.Companion.toCourse
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreCourseContract
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object FirestoreCourseRepository {
    private const val TAG = "FirestoreCourseReposito"

    fun setCourse(course: Course): Course {
        val db = FirebaseFirestore.getInstance()

        val id = db.collection(FirestoreCourseContract.COLLECTION_NAME).document().id

        course.courseId = id

        db.collection(FirestoreCourseContract.COLLECTION_NAME).document(id)
            .set(course)

        return course
    }

    suspend fun getCourse(courseId: String): Course? {
        val db = FirebaseFirestore.getInstance()

        val coursesRef = db.collection(FirestoreCourseContract.COLLECTION_NAME).document(courseId)

        return try {
            coursesRef.get().await().toCourse()
        } catch (e: Exception) {
            Log.e(TAG, "getCourse: ", e)
            null
        }
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