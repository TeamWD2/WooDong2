package com.wd.woodong2.presentation.group.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class GroupItem(
    open val title: String?
): Parcelable {
    @Parcelize
    data class GroupMain(
        override val title: String?,
        val groupName: String?,
        val groupTag: String?,
        val ageLimit: String?,
        val memberLimit: String?,
        val password: String?,
        val mainImage: String?,
        val backgroundImage: String?,
        val memberCount: Int?,
        val boardCount: Int?
    ) : GroupItem(title), Parcelable

    @Parcelize
    data class GroupIntroduce(
        override val title: String?,
        val introduce: String?,
        val groupTag: String?,
        val ageLimit: String?,
        val memberLimit: String?
    ) : GroupItem(title), Parcelable

    @Parcelize
    data class GroupMember(
        override val title: String?,
        val memberList: List<Member>?
    ) : GroupItem(title), Parcelable

    @Parcelize
    data class Member(
        val userId: String?,
        val profile: String?,
        val name: String?,
        val location: String?
    ) : Parcelable

    @Parcelize
    data class GroupBoard(
        override val title: String?,
        val boardList: List<Board>?
    ) : GroupItem(title), Parcelable

    @Parcelize
    data class Board(
        val userId: String?,
        val profile: String?,
        val name: String?,
        val location: String?,
        val timestamp: Long,
        val content: String?,
        val images: List<String>?
    ) : Parcelable

    @Parcelize
    data class GroupAlbum(
        override val title: String?,
        val images: List<String>?
    ) : GroupItem(title), Parcelable
}