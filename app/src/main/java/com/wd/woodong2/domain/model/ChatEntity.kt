package com.wd.woodong2.domain.model


data class ChatItemsEntity(
    val chatItems: List<ChatEntity>?
)

data class ChatEntity(
    val id: String?,
    val groupId: String?,
    val last: Message?,
    val mainImage: String?,
    val memberLimit: String?,
    val message: List<Message>?,
    val title: String?,
)

data class Message(
    val content: String?,
    val timestamp: Long?,
    val senderId: String?,
)