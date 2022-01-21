package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.friend.Friend
import com.piotrokninski.teacherassistant.model.friend.Friend.Companion.toFriend
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreFriendContract
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreUserContract
import kotlinx.coroutines.tasks.await

object FirestoreFriendRepository {
    private const val TAG = "FirestoreFriendReposito"

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

        val friendsQuery = friendsCollectionRef.whereEqualTo(
            FirestoreFriendContract.STATUS,
            FirestoreFriendContract.STATUS_APPROVED
        )

        try {
            friendsQuery.get().await().forEach { friend ->
                friend?.toFriend()?.let {
                    if (it.friendshipType == friendshipType) {
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