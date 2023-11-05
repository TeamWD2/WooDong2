package com.wd.woodong2.domain.model


data class MessageItemsEntity(
    val messageItems: List<MessageEntity>?,
)

data class MessageEntity(
    val id: String?,
    val senderId: String?,
    val timestamp: Long?,
    val content: String?,
    val nickname: String?,
)