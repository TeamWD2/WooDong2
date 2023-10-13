package com.wd.woodong2.presentation.chat.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ChatItem : Parcelable {
    data class GroupChatItem(
        val title: String?,
        val thumbnail: String?,
        val location: String?,
        val contents: String?,
        val timeStamp: String?,
    ) : ChatItem()
}
