package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.course.Homework
import com.piotrokninski.teacherassistant.model.course.Homework.Companion.toHomework
import com.piotrokninski.teacherassistant.util.AppConstants
import kotlinx.coroutines.tasks.await
import kotlin.collections.ArrayList

object FirestoreHomeworkRepository {
    private const val TAG = "FirestoreHomeworkReposi"

    fun addHomework(homework: Homework) {
        val db = FirebaseFirestore.getInstance()

        db.collection(Homework.Contract.COLLECTION_NAME).add(homework)
    }

    suspend fun getHomework(userId: String, idField: String) = getHomework(userId, idField, null)

    suspend fun getHomework(
        userId: String,
        idField: String,
        status: String?
    ): ArrayList<Homework>? {
        val db = FirebaseFirestore.getInstance()

        val homeworksRef = db.collection(Homework.Contract.COLLECTION_NAME)

        val query = if (status != null) {
            homeworksRef.whereEqualTo(idField, userId)
                .whereEqualTo(Homework.Contract.STATUS, status)
        } else {
            homeworksRef.whereEqualTo(idField, userId)
        }

        return try {

            val homeworkList = ArrayList<Homework>()

            query.get().await().forEach { homework ->
                homework?.toHomework()?.let {
                    Log.d(TAG, "getHomework: ${it.subject}")
                    homeworkList.add(it)
                }
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