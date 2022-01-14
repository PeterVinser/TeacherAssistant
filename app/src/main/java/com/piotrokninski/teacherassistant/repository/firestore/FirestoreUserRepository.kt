package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreUserContract
import com.piotrokninski.teacherassistant.model.user.User
import com.piotrokninski.teacherassistant.model.user.User.Companion.toUser
import com.piotrokninski.teacherassistant.model.user.UserHint
import kotlinx.coroutines.tasks.await

object FirestoreUserRepository {
    private const val TAG = "FirestoreUserRepository"

    fun setUserData(user: User) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreUserContract.COLLECTION_NAME).document(user.userId)
            .set(user)
    }

    suspend fun getUserDataOnce(userId: String): User? {
        val db = FirebaseFirestore.getInstance()

        val usersCollectionRef = db.collection(FirestoreUserContract.COLLECTION_NAME)

        return try {
            usersCollectionRef.document(userId).get().await().toUser()
        } catch (e: Exception) {
            Log.e(TAG, "getUserDataOnce: error:", e)
            null
        }
    }

    suspend fun getUsers(hints: ArrayList<UserHint>): ArrayList<User> {
        val db = FirebaseFirestore.getInstance()

        //TODO change the return to return try and add nullable return type
        val users = ArrayList<User>()

        val usersCollectionRef = db.collection(FirestoreUserContract.COLLECTION_NAME)

        hints.forEach { hint ->

            try {
                val user = usersCollectionRef.document(hint.userId).get().await().toUser()

                users.add(user!!)
            } catch (e: Exception) {
                Log.e(TAG, "getSearchedUsers: error: ", e)
            }
        }

        return users
    }
}