package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreHomeworkContract
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.model.course.Homework.Companion.toHomework
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.tasks.await
import java.lang.IllegalArgumentException

object FirestoreHomeworkRepository {
    private const val TAG = "FirestoreHomeworkReposi"

    fun addHomework(homework: Homework) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreHomeworkContract.COLLECTION_NAME).add(homework)
    }

    suspend fun getHomework(
        userId: String,
        viewType: String,
        status: String
    ): ArrayList<Homework>? {
        val db = FirebaseFirestore.getInstance()

        val homeworksRef = db.collection(FirestoreHomeworkContract.COLLECTION_NAME)

        val query = when (viewType) {
            AppConstants.VIEW_TYPE_STUDENT -> homeworksRef.whereEqualTo(
                FirestoreHomeworkContract.STUDENT_ID,
                userId
            ).whereEqualTo(FirestoreHomeworkContract.STATUS, status)

            AppConstants.VIEW_TYPE_TUTOR -> homeworksRef.whereEqualTo(
                FirestoreHomeworkContract.TUTOR_ID,
                userId
            ).whereEqualTo(FirestoreHomeworkContract.STATUS, status)

            else -> throw IllegalArgumentException("Unknown viewType")
        }

        return try {

            val homeworkList = ArrayList<Homework>()

            query.get().await().forEach { homework ->
                homework?.toHomework()?.let { homeworkList.add(it) }
            }

            if (homeworkList.isEmpty()) {
                null
            } else {
                homeworkList
            }

        } catch (e: Exception) {
            Log.e(TAG, "getHomeworks: ", e)
            null
        }
    }
}