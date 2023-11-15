package com.wd.woodong2.services.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wd.woodong2.R


class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG: String = "MyFirebaseMsgService"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // sharedPreferences X 토큰을 저장 <- 휘발성 데이터
        // 토큰을 user_db에 계속 업데이트 해야 됨
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
    }

    private fun showNotification(title: String?, body: String?) {
        val channelId = "General notification"
        val channelName = "New User"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0(Oreo) 이상에서는 채널을 만들어야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.wd2) // 앱 아이콘 설정
            .setAutoCancel(true)

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}