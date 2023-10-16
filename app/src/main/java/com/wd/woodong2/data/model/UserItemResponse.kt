package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName

data class UserItemsResponse(
    val groupItems: List<UserResponse>?
)

data class UserResponse(
    val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("imgProfile") val imgProfile: String?,
    @SerializedName("email") val email: String?,
    // 유저가 보유한 채팅방 IDs
    @SerializedName("chatIds") val chatIds: List<String>?,
)