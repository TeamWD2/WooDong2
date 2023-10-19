package com.wd.woodong2.domain.model


data class ChatItemsEntity(
    val chatItems: List<ChatEntity>?
)

data class ChatEntity(
    val id: String?,
    val imgProfile: String?,
    val senderId: String?,
    val location: String?,
    val timestamp: Long?,
    val lastMessage: String?,
    val message: Map<String, Message>?,
)

data class Message(
    val id: String?,
    val message: String?,
    val timestamp: Long?,
    val senderId: String?,
)