package com.wd.woodong2.presentation.group.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class GroupItem(
    open val id: String?,
    open val title: String?
) : Parcelable {
    @Parcelize
    data class GroupMain(
        override val id: String?,
        override val title: String?,
        val groupName: String?,
        val introduce: String?,
        val groupTag: String?,
        val ageLimit: String?,
        val memberLimit: String?,
        val password: String?,
        val mainImage: String?,
        val backgroundImage: String?,
        val groupLocation: String?
    ) : GroupItem(id, title), Parcelable

    @Parcelize
    data class GroupIntroduce(
        override val id: String?,
        override val title: String?,
        val introduce: String?,
        val groupTag: String?,
        val ageLimit: String?,
        val memberLimit: String?
    ) : GroupItem(id, title), Parcelable

    @Parcelize
    data class GroupMember(
        override val id: String?,
        override val title: String?,
        val memberList: List<Member>?
    ) : GroupItem(id, title), Parcelable

    @Parcelize
    data class Member(
        val userId: String?,
        val profile: String?,
        val name: String?,
        val location: String?,
        val comment: String?
    ) : Parcelable

    @Parcelize
    data class GroupBoard(
        override val id: String?,
        override val title: String?,
        val boardList: List<Board>?
    ) : GroupItem(id, title), Parcelable

    @Parcelize
    data class Board(
        val boardId: String?,
        val userId: String?,
        val profile: String?,
        val name: String?,
        val location: String?,
        val timestamp: Long,
        val content: String?,
        val images: List<String>?,
        val commentList: List<BoardComment>?
    ) : Parcelable

    @Parcelize
    data class BoardComment(
        var commentId: String?,
        val userId: String?,
        val userProfile: String?,
        val userName: String?,
        val userLocation: String?,
        val timestamp: Long?,
        val comment: String?,
    ) : Parcelable

    @Parcelize
    data class GroupAlbum(
        override val id: String?,
        override val title: String?,
        val images: List<String>?
    ) : GroupItem(id, title), Parcelable
}