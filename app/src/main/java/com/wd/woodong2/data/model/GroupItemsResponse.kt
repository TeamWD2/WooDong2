package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName

data class GroupItemsResponse(
    val groupItems: List<GroupResponse>?
)

data class GroupResponse(
    val id: String?, //Firebase Realtime Database에서 자동 생성되는 고유 키
    @SerializedName("title") val title: String?,
    @SerializedName("introduce") val introduce: String?,
    @SerializedName("groupTag") val groupTag: String?,
    @SerializedName("ageLimit") val ageLimit: String?,
    @SerializedName("memberLimit") val memberLimit: String?,
    @SerializedName("memberList") val memberList: List<MemberResponse>?,
    @SerializedName("password") val password: String?,
    @SerializedName("mainImage") val mainImage: String?,
    @SerializedName("backgroundImage") val backgroundImage: String?
)

data class MemberResponse(
    @SerializedName("userId") val userId: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("userProfile") val userProfile: String?
)