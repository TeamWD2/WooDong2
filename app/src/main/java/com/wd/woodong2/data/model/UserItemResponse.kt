package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName

data class UserItemsResponse(
    val groupItems: List<UserResponse>?,
)

data class UserResponse(
    val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("imgProfile") val imgProfile: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("chatIds") val chatIds: Map<String, String>?,        // 유저가 보유한 채팅방 IDs        ->map으로 변경해야된다
    @SerializedName("firstLocation") val firstLocation: String?,         // 현재 사용자 위치 설정
    @SerializedName("secondLocation") val secondLocation: String?,      // 현재 사용자 위치 설정
    @SerializedName("token") val token: String?,         // 현재 사용자 위치 설정
)