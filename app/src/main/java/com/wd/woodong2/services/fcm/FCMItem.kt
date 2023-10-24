package com.wd.woodong2.services.fcm

data class FCMNotification(
    val to: String, // 수신자의 FCM 토큰
    val data: Map<String, String>,
    val notification: Map<String, String>, // Title, Body
)