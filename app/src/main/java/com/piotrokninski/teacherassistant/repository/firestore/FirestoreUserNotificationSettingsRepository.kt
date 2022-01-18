package com.piotrokninski.teacherassistant.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreUserNotificationSettingsContract
import com.piotrokninski.teacherassistant.model.user.UserNotificationSettings
import com.piotrokninski.teacherassistant.model.user.UserNotificationSettings.Companion.toUserNotificationSettings
import kotlinx.coroutines.tasks.await

object FirestoreUserNotificationSettingsRepository {
    private const val TAG = "FirestoreUserNotificati"

    fun setUserNotificationSettings(userId: String, userNotificationSettings: UserNotificationSettings) {
        val db = FirebaseFirestore.getInstance()

        db.collection(FirestoreUserNotificationSettingsContract.COLLECTION_NAME).document(userId)
            .set(userNotificationSettings)

        Log.d(TAG, "setUserNotificationSettings: the token has been set")
    }

    fun updateNotificationToken(userId: String, token: String, deviceAvailable: Boolean) {
        val db = FirebaseFirestore.getInstance()

        val updateToken = mapOf(
            "tokens.$token" to deviceAvailable
        )

        db.collection(FirestoreUserNotificationSettingsContract.COLLECTION_NAME).document(userId)
            .update(updateToken)

        Log.d(TAG, "updateNotificationToken: token updated")
    }

    suspend fun getUserNotificationSettings(userId: String): UserNotificationSettings? {
        val db = FirebaseFirestore.getInstance()

        val notificationSettingsCollectionRef =
            db.collection(FirestoreUserNotificationSettingsContract.COLLECTION_NAME)

        return try {
            notificationSettingsCollectionRef.document(userId).get().await().toUserNotificationSettings()
        } catch (e: Exception) {
            Log.e(TAG, "getUserNotificationSettings: ", e)
            null
        }
    }


}