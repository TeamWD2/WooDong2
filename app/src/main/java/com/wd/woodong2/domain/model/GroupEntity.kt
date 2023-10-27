package com.wd.woodong2.domain.model

sealed interface GroupEntity
data class GroupItemsEntity(
    val groupList: List<GroupEntity>
)

/**
 * 메인
 */
data class GroupMainEntity(
    val id: String?,
    val groupName: String?,
    val groupTag: String?,
    val ageLimit: String?,
    val memberLimit: String?,
    val password: String?,
    val mainImage: String?,
    val backgroundImage: String?,
    val memberCount: Int?,
    val boardCount: Int?
) : GroupEntity

/**
 * 소개
 */
data class GroupIntroduceEntity(
    val id: String?,
    val title: String?,
    val introduce: String?,
    val groupTag: String?,
    val ageLimit: String?,
    val memberLimit: String?,
) : GroupEntity

/**
 * 멤버
 */
data class GroupMemberEntity(
    val id: String?,
    val title: String?,
    val memberList: List<GroupMemberItemEntity>?
) : GroupEntity

/**
 * 멤버 아이템
 */
data class GroupMemberItemEntity(
    val userId: String?,
    val profile: String?,
    val name: String?,
    val location: String?
)

/**
 * 게시판
 */
data class GroupBoardEntity(
    val id: String?,
    val title: String?,
    val boardList: List<GroupBoardItemEntity>?
) : GroupEntity

/**
 * 게시판 아이템
 */
data class GroupBoardItemEntity(
    val userId: String?,
    val profile: String?,
    val name: String?,
    val location: String?,
    val timestamp: Long,
    val content: String?,
    val images: List<String>?,
)

/**
 * 앨범
 */
data class GroupAlbumEntity(
    val id: String?,
    val title: String?,
    val images: List<String>?
) : GroupEntity