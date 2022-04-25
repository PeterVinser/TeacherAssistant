package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.friend.Friend.Companion.toFriend
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreUserContract
import kotlinx.coroutines.tasks.await

object FirestoreFriendRepository {
    private const val TAG = "FirestoreFriendReposito"

    fun deleteFriend(userId: String, friendId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
            .collection(FirestoreFriendContract.COLLECTION_NAME).document(friendId)
            .delete()
    }

    suspend fun getFriendDataOnce(userId: String, friendId: String): Friend? {
        val db = FirebaseFirestore.getInstance()

        val friendsCollectionRef =
            db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
                .collection(FirestoreFriendContract.COLLECTION_NAME).document(friendId)

        return try {
            friendsCollectionRef.get().await().toFriend()
        } catch (e: Exception) {
            Log.e(TAG, "getFriendDataOnce: ", e)
            null
        }
    }

    suspend fun getApprovedFriends(userId: String, friendshipType: String): ArrayList<Friend> {
        val db = FirebaseFirestore.getInstance()

        val friends = ArrayList<Friend>()

        val friendsCollectionRef =
            db.collection(FirestoreUserContract.COLLECTION_NAME).document(userId)
                .collection(FirestoreFriendContract.COLLECTION_NAME)

        val friendsQuery = if (friendshipType != FirestoreFriendContract.TYPE_ALL) {
            friendsCollectionRef.whereEqualTo(
                FirestoreFriendContract.STATUS,
                FirestoreFriendContract.STATUS_APPROVED
            ).whereEqualTo(FirestoreFriendContract.FRIENDSHIP_TYPE, friendshipType)
        } else {
            friendsCollectionRef.whereEqualTo(
                FirestoreFriendContract.STATUS,
                FirestoreFriendContract.STATUS_APPROVED
            )
        }

        return try {
            friendsQuery.get().await().forEach { friend ->
                friend?.toFriend()?.let {
                    friends.add(it)
                    Log.d(TAG, "getApprovedFriends: ${it.fullName}")
                }
            }

            friends
        } catch (e: Exception) {
            Log.e(TAG, "getAllFriends: ", e)
            friends
        }
    }
}