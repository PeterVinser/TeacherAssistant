package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.Friend
import com.piotrokninski.teacherassistant.model.Friend.Companion.toFriend
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreUserContract
import kotlinx.coroutines.tasks.await

object FirestoreFriendRepository {
    private const val TAG = "FirestoreFriendReposito"

    fun setFriendData(userId: String, friend: Friend) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
            .collection(FirestoreFriendContract.COLLECTION_NAME).document(friend.userId)
            .set(friend)
    }

    fun updateFriendshipType(userId: String, friendId: String, friendshipType: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
            .collection(FirestoreFriendContract.COLLECTION_NAME).document(friendId)
            .update(FirestoreFriendContract.FRIENDSHIP_TYPE, friendshipType)
    }

    fun updateFriendshipStatus(userId: String, friendId: String, status: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
            .collection(FirestoreFriendContract.COLLECTION_NAME).document(friendId)
            .update(FirestoreFriendContract.STATUS, status)
    }

    fun deleteFriendData(userId: String, friendId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
            .collection(FirestoreFriendContract.COLLECTION_NAME).document(friendId)
            .delete()
    }

    suspend fun getFriendDataOnce(userId: String, friendId: String): Friend? {
        val db = FirebaseFirestore.getInstance()

        val friendsCollectionRef = db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
            .collection(FirestoreFriendContract.COLLECTION_NAME).document(friendId)

        return try {
            friendsCollectionRef.get().await().toFriend()
        } catch (e: Exception) {
            Log.e(TAG, "getFriendDataOnce: ", e)
            null
        }
    }

    suspend fun getFriends(userId: String, status: String): ArrayList<Friend> {
        val db = FirebaseFirestore.getInstance()

        val friends = ArrayList<Friend>()

        val friendsCollectionRef = db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
            .collection(FirestoreFriendContract.COLLECTION_NAME)

        try {
            friendsCollectionRef.get().await().forEach { friend ->
                friend?.toFriend()?.let {
                    if (it.status == status) {
                        friends.add(it)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllFriends: ", e)
        }

        return friends
    }
}