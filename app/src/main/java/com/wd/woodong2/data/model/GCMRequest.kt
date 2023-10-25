package com.wd.woodong2.data.model

data class GCMRequest(
    val to: String, // 수신자의 FCM 토큰
    val data: Map<String, String>, // 담아서 보낼 정보
    val notification: Map<String, String>, // Title, Body
)