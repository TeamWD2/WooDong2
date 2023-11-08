package com.wd.woodong2.presentation.chat.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ChatItem : Parcelable {
    abstract val id: String?

    data class GroupChatItem(
        override val id: String?,
        val groupId: String?,
        val lastMessage: String?,
        val timeStamp: Long?,
        val mainImage: String?,
        val memberLimit: String?,
        val title: String?,
        val isRead: Boolean?,
    ) : ChatItem()

    data class PrivateChatItem(
        override val id: String?,
        val groupId: String?,
        val lastMessage: String?,
        val timeStamp: Long?,
        val mainImage: String?,
        val memberLimit: String?,
        val title: String?,
    ) : ChatItem()
}
