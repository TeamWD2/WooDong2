package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName

data class ChatItemsResponse(
    val chatItems: List<ChatResponse>?,
)

data class ChatResponse(
    val id: String?,
    @SerializedName("groupId") val groupId: String?,
    @SerializedName("last") val last: MessageResponse?,
    @SerializedName("mainImage") val mainImage: String?,
    @SerializedName("memberLimit") val memberLimit: String?,
    @SerializedName("message") val message: Map<String, MessageResponse>?,
    @SerializedName("title") val title: String?
)
data class MessageResponse(
    val id: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("senderId") val senderId: String?,
    @SerializedName("timestamp") val timestamp: Long?
)