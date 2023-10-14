package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName

data class ChatItemsResponse(
    val chatItems: List<ChatResponse>?
)

// ChatFragment에서 사용할 데이터 구조
data class ChatResponse(
    val id: String?,
    @SerializedName("imgProfile") val imgProfile: String?,
    // 그룹이거나 사람
    @SerializedName("sender") val sender: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("lastMassage") val lastMassage: String?,
)