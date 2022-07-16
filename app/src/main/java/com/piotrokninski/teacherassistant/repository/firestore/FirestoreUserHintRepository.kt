package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.user.UserHint
import com.piotrokninski.teacherassistant.model.user.UserHint.Companion.toUserHint
import kotlinx.coroutines.tasks.await

object FirestoreUserHintRepository {
    private val TAG = "FirestoreUserHintReposi"

    fun setUserHintData(userHint: UserHint) {
        val db = FirebaseFirestore.getInstance()

        db.collection(UserHint.Contract.COLLECTION_NAME).document(userHint.userId)
            .set(userHint)
    }

    suspend fun getUsersHints(keyword: String): ArrayList<UserHint> {
        val db = FirebaseFirestore.getInstance()

        //TODO change the return to return try and add nullable return type
        val hints = ArrayList<UserHint>()

        val usersCollectionRef = db.collection(UserHint.Contract.COLLECTION_NAME)

        val usersQuery = usersCollectionRef
            .whereArrayContains("keywords", keyword)

        try {
            usersQuery.get().await().forEach { hint ->
                hint?.toUserHint()?.let { hints.add(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getSearchedUsers: error: ", e)
        }

        return hints
    }
}