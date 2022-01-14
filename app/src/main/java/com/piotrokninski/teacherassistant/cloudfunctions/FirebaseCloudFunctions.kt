package com.piotrokninski.teacherassistant.cloudfunctions

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.piotrokninski.teacherassistant.model.friend.FriendInvitation
import com.piotrokninski.teacherassistant.util.Util.serializeToMap

object FirebaseCloudFunctions {
    private const val TAG = "FirebaseCloudFunctions"

    fun sendInvitation(friendInvitation: FriendInvitation) {
        val functions = FirebaseFunctions.getInstance("europe-west1")

        val invitationData = friendInvitation.serializeToMap()

        functions
            .getHttpsCallable("sendFriendInvitation")
            .call(invitationData)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    Log.e(TAG, "sendInvitation: ", e)
                }
            }
    }
}