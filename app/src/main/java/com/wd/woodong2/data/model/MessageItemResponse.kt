package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName

data class MessageItemsResponse(
    val messageItems: List<MessageResponse>?
)

data class MessageResponse(
    @SerializedName("id") val id: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: String?,
    @SerializedName("senderId") val senderId: String?,
)