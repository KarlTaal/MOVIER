package com.example.movierproject

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService: FirebaseMessagingService() {

    companion object {
        const val INTENT_ACTION_SEND_MESSAGE = "INTENT_ACTION_SEND_MESSAGE"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token);
        getSharedPreferences(getString(R.string.preferences_file), MODE_PRIVATE).edit().putString("token", token).apply();
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val movieId = remoteMessage.data["movieId"]
        println("MOVIEID: $movieId")
        passMessageToActivity(movieId)
    }

    private fun passMessageToActivity(movieId: String?) {
        val intent = Intent().apply {
            action = INTENT_ACTION_SEND_MESSAGE
            putExtra("movieId", movieId)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}
