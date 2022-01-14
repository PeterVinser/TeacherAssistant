package com.piotrokninski.teacherassistant.util.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.view.main.MainActivity

class AppFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "AppFirebaseMessagingSer"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "onMessageReceived, payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "onMessageReceived, body: ${it.body}")
            sendNotification(it.body.toString())
        }
    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(channelId, 1, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken: refreshed token: $token")

        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        //TODO implement the method
    }
}