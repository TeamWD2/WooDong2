package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName
import com.wd.woodong2.domain.model.Massage

data class ChatItemsResponse(
    val chatItems: List<ChatResponse>?
)

// ChatFragment에서 사용할 데이터 구조
data class ChatResponse(
    val id: String?,
    @SerializedName("imgProfile") val imgProfile: String?,
    // 그룹이거나 사람
    @SerializedName("senderId") val senderId: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("lastMassage") val lastMassage: String?,
    @SerializedName("massage") val massage: List<Massage>?,
)

data class MassageResponse(
    val id: String?,
    val message: String?,
    val timestamp: String?,
    @SerializedName("senderId") val senderId: String?,
)