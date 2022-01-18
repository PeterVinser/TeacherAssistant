package com.piotrokninski.teacherassistant.util.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object FcmManager {
    private const val TAG = "FcmManager"

    fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Subscription successful"

                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }

                Log.d(TAG, "onTopicSubscribeClicked: $msg")
            }
    }

    fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Subscription successful"

                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }

                Log.d(TAG, "onTopicSubscribeClicked: $msg")
            }
    }

}