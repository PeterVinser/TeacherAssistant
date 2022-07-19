package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.friend.Friend.Companion.toFriend
import com.piotrokninski.teacherassistant.model.user.User
import kotlinx.coroutines.tasks.await

object FirestoreFriendRepository {
    private const val TAG = "FirestoreFriendReposito"

    fun deleteFriend(userId: String, friendId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection(User.Contract.COLLECTION_NAME).document(userId)
            .collection(Friend.Contract.COLLECTION_NAME).document(friendId)
            .delete()
    }

    suspend fun getFriendDataOnce(userId: String, friendId: String): Friend? {
        val db = FirebaseFirestore.getInstance()

        val friendsCollectionRef =
            db.collection(User.Contract.COLLECTION_NAME).document(userId)
                .collection(Friend.Contract.COLLECTION_NAME).document(friendId)

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
            db.collection(User.Contract.COLLECTION_NAME).document(userId)
                .collection(Friend.Contract.COLLECTION_NAME)

        val friendsQuery = if (friendshipType != Friend.Contract.TYPE_ALL) {
            friendsCollectionRef.whereEqualTo(
                Friend.Contract.STATUS,
                Friend.Contract.STATUS_APPROVED
            ).whereEqualTo(Friend.Contract.FRIENDSHIP_TYPE, friendshipType)
        } else {
            friendsCollectionRef.whereEqualTo(
                Friend.Contract.STATUS,
                Friend.Contract.STATUS_APPROVED
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