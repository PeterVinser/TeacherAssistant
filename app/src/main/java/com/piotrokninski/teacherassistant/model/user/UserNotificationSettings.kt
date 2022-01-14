package com.piotrokninski.teacherassistant.model.user

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.piotrokninski.teacherassistant.model.contract.firestore.FirestoreUserNotificationSettingsContract
import com.piotrokninski.teacherassistant.repository.firestore.FirestoreUserNotificationSettingsRepository
import java.lang.Exception
import java.lang.IllegalStateException

data class UserNotificationSettings(val tokens: Map<String, Boolean>) {

    companion object {

        fun DocumentSnapshot.toUserNotificationSettings(): UserNotificationSettings? {
            return try {
                val tokens = get(FirestoreUserNotificationSettingsContract.TOKENS) as Map<String, Boolean>

                UserNotificationSettings(tokens)
            } catch (e: Exception) {
                Log.e(TAG, "toUserNotificationSettings: ", e)
                null
            }
        }

        suspend fun setDeviceNotificationToken(userId: String, deviceAvailable: Boolean) {

            val token = FirebaseMessaging.getInstance().token.result

            if (token != null) {
                Log.d(TAG, "setDeviceNotificationToken: $token")

                var userNotificationSettings = FirestoreUserNotificationSettingsRepository.getUserNotificationSettings(userId)

                if (userNotificationSettings == null) {
                    userNotificationSettings = UserNotificationSettings(mapOf(
                        token to deviceAvailable
                    ))

                    FirestoreUserNotificationSettingsRepository.setUserNotificationSettings(userId, userNotificationSettings)
                } else {
                    FirestoreUserNotificationSettingsRepository.updateNotificationToken(userId, token, deviceAvailable)
                }
            } else {
                Log.e(TAG, "setDeviceNotificationToken: the token has not been found")
            }

        }

        private const val TAG = "UserNotificationSetting"
    }
}
