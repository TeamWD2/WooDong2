package com.wd.woodong2.data.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

sealed interface GroupResponse

@JsonAdapter(GroupItemsResponseJsonDeserializer::class)
data class GroupItemsResponse(
    @SerializedName("group_list") val groupList: List<GroupResponse>
)

/**
 * 메인
 */
data class GroupMainResponse(
    val id: String?,
    @SerializedName("groupName") val groupName: String?,
    @SerializedName("introduce") val introduce: String?,
    @SerializedName("groupTag") val groupTag: String?,
    @SerializedName("ageLimit") val ageLimit: String?,
    @SerializedName("memberLimit") val memberLimit: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("mainImage") val mainImage: String?,
    @SerializedName("backgroundImage") val backgroundImage: String?,
    @SerializedName("memberCount") val memberCount: Int?,
    @SerializedName("boardCount") val boardCount: Int?
) : GroupResponse
/**
 * 소개
 */
data class GroupIntroduceResponse(
    val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("introduce") val introduce: String?,
    @SerializedName("groupTag") val groupTag: String?,
    @SerializedName("ageLimit") val ageLimit: String?,
    @SerializedName("memberLimit") val memberLimit: String?
) : GroupResponse

/**
 * 멤버
 */
data class GroupMemberResponse(
    val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("memberList") val memberList: List<GroupMemberItemResponse>?
) : GroupResponse

/**
 * 멤버 아이템
 */
data class GroupMemberItemResponse(
    @SerializedName("userId") val userId: String?,
    @SerializedName("profile") val profile: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("comment") val comment: String?
)

/**
 * 게시판
 */
data class GroupBoardResponse(
    val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("boardList") val boardList: Map<String, GroupBoardItemResponse>?
) : GroupResponse

/**
 * 게시판 아이템
 */
data class GroupBoardItemResponse(
    @SerializedName("userId") val userId: String?,
    @SerializedName("profile") val profile: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("content") val content: String?,
    @SerializedName("images") val images: List<String>?,
    @SerializedName("commentList") val commentList: Map<String, GroupBoardCommentResponse>?
)

/**
 * 게시판 댓글
 */
data class GroupBoardCommentResponse(
    @SerializedName("userId") val userId: String?,
    @SerializedName("userProfile") val userProfile: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("userLocation") val userLocation: String?,
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("comment") val comment: String?,
)

/**
 * 앨범
 */
data class GroupAlbumResponse(
    val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("images") val images: Map<String, String>?
) : GroupResponse