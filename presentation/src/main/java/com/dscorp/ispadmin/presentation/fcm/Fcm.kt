package com.dscorp.gigafiberperu.presentation.firebase.fcm

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging


private const val TAG = "MainActivity"
fun Context.getFcmToken(
    onTokenReceived: (token: String) -> Unit,
) =
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            return@addOnCompleteListener
        }
        // Get new FCM registration token
        val token = task.result
        onTokenReceived(token)
    }

