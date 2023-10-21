package com.wd.woodong2.services.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wd.woodong2.R
import com.wd.woodong2.presentation.chat.content.ChatItem


class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG: String = "MyFirebaseMsgService"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 토큰이 업데이트될 때마다 호출됩니다.
        // 새로운 토큰을 서버에 보내거나 저장할 수 있습니다.

        Log.d(TAG, token)

        // sharedPreferences X 토큰을 저장 <- 휘발성 데이터
        // 토큰을 user_db에 계속 업데이트 해야 됨

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "remoteMessage Title: " + remoteMessage.notification?.title)
        Log.d(TAG, "remoteMessage Body: " + remoteMessage.notification?.body)

        val noti = remoteMessage.notification

        // Test
        val chatItem = ChatItem.GroupChatItem(
            id = "-chat_list-group-TestData1",
            groupId = "groupId",
            lastMessage = "lastMessage",
            timeStamp = 12510436307,
            mainImage = "mainImage",
            memberLimit = "memberLimit",
            title = "Test title",
        )
        val userId = "user1"

        val intent = Intent("com.wd.woodong2.OPEN_CHAT").apply {
            addCategory("android.intent.category.DEFAULT")
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("chat_item", chatItem)
            putExtra("user_id", userId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // 액션을 넘길 수 있다(클래스 이름) manifest에 정의해서 Action을 통해 특정 액티비티를 실행시킬 수 있는 방법
        val notificationBuilder = NotificationCompat.Builder(this, "chat_message_id")
            .setSmallIcon(R.drawable.wd2)
            .setContentTitle(noti?.title)
            .setContentText(noti?.body)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // 알림 사운드 설정
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification Channel 생성 (Android 8.0 이상에서 필요)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "chat_message_id",
                "Chat Message",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}