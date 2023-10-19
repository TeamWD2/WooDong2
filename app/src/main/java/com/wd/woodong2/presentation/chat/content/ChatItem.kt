package com.wd.woodong2.presentation.chat.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ChatItem : Parcelable {
    abstract val id: String?

    data class GroupChatItem(
        override val id: String?,
        val title: String?,
        val imgProfile: String?,
        val location: String?,
        val timeStamp: String?,
        val lastMessage: String?,
    ) : ChatItem()

    data class PrivateChatItem(
        override val id: String?,
        val userName: String?,
        val imgProfile: String?,
        val location: String?,
        val timeStamp: String?,
        val lastMessage: String?,
    ) : ChatItem()
}
