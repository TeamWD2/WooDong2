package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName

data class GroupItemsResponse(
    val groupItems: List<GroupResponse>
)

data class GroupResponse(
    val id: String?, //Firebase Realtime Database에서 자동 생성되는 고유 키
    @SerializedName("introduce") val introduce: GroupIntroduceResponse?,
    @SerializedName("member") val member: GroupMemberResponse?,
    @SerializedName("board") val board: GroupBoardResponse?,
)

/**
 * 소개
 */
data class GroupIntroduceResponse(
    @SerializedName("title") val title: String?,
    @SerializedName("introduce") val introduce: String?,
    @SerializedName("groupTag") val groupTag: String?,
    @SerializedName("ageLimit") val ageLimit: String?,
    @SerializedName("memberLimit") val memberLimit: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("mainImage") val mainImage: String?,
    @SerializedName("backgroundImage") val backgroundImage: String?,
    @SerializedName("timestamp") val timestamp: Long
)

/**
 * 멤버
 */
data class GroupMemberResponse(
    @SerializedName("memberList") val memberList: List<MemberItemResponse>?
)

/**
 * 멤버 아이템
 */
data class MemberItemResponse(
    @SerializedName("userId") val userId: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("userProfile") val userProfile: String?
)

/**
 * 게시판
 */
data class GroupBoardResponse(
    @SerializedName("boardList") val boardList: List<BoardItemResponse>?
)

/**
 * 게시판 아이템
 */
data class BoardItemResponse(
    @SerializedName("userId") val userId: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("userProfile") val userProfile: String?,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("content") val content: String?,
    @SerializedName("photoList") val photoList: List<PhotoItemResponse>?,
)

/**
 * 게시물 사진 아이템
 */
data class PhotoItemResponse(
    @SerializedName("photo") val photo: String?
)