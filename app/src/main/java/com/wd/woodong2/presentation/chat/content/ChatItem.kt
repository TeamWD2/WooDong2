package com.wd.woodong2.presentation.chat.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ChatItem : Parcelable {
    data class GroupChatItem(
        val title: String?,
        val imgProfile: String?,
        val location: String?,
        val timeStamp: String?,
        val lastMassage: String?,
    ) : ChatItem()

    data class PrivateChatItem(
        val userName: String?,
        val imgProfile: String?,
        val location: String?,
        val timeStamp: String?,
        val lastMassage: String?,
    ) : ChatItem()
}
