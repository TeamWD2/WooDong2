package com.wd.woodong2.presentation.chat.content

// 추후 개인 채팅 추가를 위해 sealed로 선언
sealed class ChatItem {
    data class GroupChatItem(
        val title: String?,
        val thumbnail: String?,
        val location: String?,
        val contents: String?,
        val timeStamp: String?,
    ) : ChatItem()
}