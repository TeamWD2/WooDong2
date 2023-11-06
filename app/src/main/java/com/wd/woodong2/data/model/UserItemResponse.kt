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
    @SerializedName("chatIds") val chatIds: List<String>?,        // 유저가 보유한 채팅방 IDs        ->map으로 변경해야된다
    @SerializedName("groupIds") val groupIds: List<String>?,        //모임
    @SerializedName("likedIds") val likedIds: List<String>?,        //좋아요 게시물
    @SerializedName("writtenIds") val writtenIds: List<String>?,        //작성한 게시물
    @SerializedName("firstLocation") val firstLocation: String?,         // 현재 사용자 위치 설정
    @SerializedName("secondLocation") val secondLocation: String?,      // 현재 사용자 위치 설정
    @SerializedName("token") val token: String?,         // 현재 사용자 위치 설정

)