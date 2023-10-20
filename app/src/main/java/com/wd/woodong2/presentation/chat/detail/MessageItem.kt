package com.wd.woodong2.presentation.chat.detail

data class MessageItem(
    val id: String?,
    val content: String?,
    val senderId: String?,
    val timestamp: Long?,
    val isMyMessage: Boolean,
)
