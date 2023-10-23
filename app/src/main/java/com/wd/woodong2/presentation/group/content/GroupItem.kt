package com.wd.woodong2.presentation.group.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupItem(
    val id: String?, //Firebase Realtime Database에서 자동 생성되는 고유 키
    val introduce: GroupIntroduce?,
    val member: GroupMember?,
    val board: GroupBoard?
): Parcelable

@Parcelize
data class GroupIntroduce(
    val title: String?,
    val introduce: String?,
    val groupTag: String?,
    val ageLimit: String?,
    val memberLimit: String?,
    val password: String?,
    val mainImage: String?,
    val backgroundImage: String?,
    val timestamp: Long?
): Parcelable

@Parcelize
data class GroupMember(
    val memberList: List<Member>?
): Parcelable

@Parcelize
data class Member(
    val userId: String?,
    val userName: String?,
    val userProfile: String?
): Parcelable

@Parcelize
data class GroupBoard(
    val boardList: List<Board>?
): Parcelable

@Parcelize
data class Board(
    val userId: String?,
    val userName: String?,
    val userProfile: String?,
    val timestamp: Long?,
    val content: String?,
    val photoList: List<Photo>?,
): Parcelable

@Parcelize
data class Photo(
    val photo: String?
): Parcelable