package com.piotrokninski.teacherassistant.cloudfunctions

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.piotrokninski.teacherassistant.model.course.Course
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.util.Util.serializeToMap

object FirebaseCloudFunctions {
    private const val TAG = "FirebaseCloudFunctions"

    private val functions = FirebaseFunctions.getInstance("europe-west1")

    fun sendFriendInvitation(friendInvitation: FriendInvitation, course: Course?) {

        val invitationData = friendInvitation.serializeToMap()

        val data = if (course != null) {
            val courseData = course.serializeToMap()

            mapOf(
                "invitation" to invitationData,
                "course" to courseData
            )
        } else {

            mapOf(
                "invitation" to invitationData
            )
        }

        functions
            .getHttpsCallable("invitation-sendFriendInvitation")
            .call(data)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    Log.e(TAG, "sendInvitation: ", e)
                }
            }
    }

    fun approveFriendInvitation(friendInvitation: FriendInvitation) {

        val invitationData = friendInvitation.serializeToMap()

        functions
            .getHttpsCallable("invitation-approveFriendInvitation")
            .call(invitationData)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    Log.e(TAG, "approveInvitation: ", e)
                }
            }
    }

    fun rejectFriendInvitation(friendInvitation: FriendInvitation) {

        val invitationData = friendInvitation.serializeToMap()

        functions
            .getHttpsCallable("invitation-rejectFriendInvitation")
            .call(invitationData)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    Log.e(TAG, "rejectFriendInvitation: ", e)
                }
            }
    }

    fun deleteFriend(firstUserId: String, secondUserId: String) {

        val data = mapOf(
            "firstUserId" to firstUserId,
            "secondUserId" to secondUserId
        )

        functions
            .getHttpsCallable("invitation-deleteFriend")
            .call(data)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    Log.e(TAG, "deleteFriend: ", e)
                }
            }
    }

    fun cancelFriendInvitation(invitingUserId: String, invitedUserId: String) {

        val data = mapOf(
            "invitingUserId" to invitingUserId,
            "invitedUserId" to invitedUserId
        )

        functions
            .getHttpsCallable("invitation-cancelFriendInvitation")
            .call(data)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    Log.e(TAG, "cancelFriendInvitation: ", e)
                }
            }
    }
}