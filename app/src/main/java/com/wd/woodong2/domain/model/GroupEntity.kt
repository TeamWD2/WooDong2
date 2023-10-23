package com.wd.woodong2.domain.model

data class GroupItemsEntity(
    val groupItems: List<GroupEntity>
)

data class GroupEntity(
    val id: String?, //Firebase Realtime Database에서 자동 생성되는 고유 키
    val introduce: GroupIntroduceEntity?,
    val member: GroupMemberEntity?,
    val board: GroupBoardEntity?
)

/**
 * 소개
 */
data class GroupIntroduceEntity(
    val title: String?,
    val introduce: String?,
    val groupTag: String?,
    val ageLimit: String?,
    val memberLimit: String?,
    val password: String?,
    val mainImage: String?,
    val backgroundImage: String?,
    val timestamp: Long
)

/**
 * 멤버
 */
data class GroupMemberEntity(
    val memberList: List<MemberItemEntity>?
)

/**
 * 멤버 아이템
 */
data class MemberItemEntity(
    val userId: String?,
    val userName: String?,
    val userProfile: String?
)

/**
 * 게시판
 */
data class GroupBoardEntity(
    val boardList: List<BoardItemEntity>?
)

/**
 * 게시판 아이템
 */
data class BoardItemEntity(
    val userId: String?,
    val userName: String?,
    val userProfile: String?,
    val timestamp: Long,
    val content: String?,
    val photoList: List<PhotoItemEntity>?,
)

/**
 * 게시물 사진 아이템
 */
data class PhotoItemEntity(
    val photo: String?
)