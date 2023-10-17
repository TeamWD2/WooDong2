package com.wd.woodong2.domain.model


data class ChatItemsEntity(
    val chatItems: List<ChatEntity>?
)

data class ChatEntity(
    val id: String?,
    val imgProfile: String?,
    val senderId: String?,
    val location: String?,
    val timestamp: String?,
    val lastMessage: String?,
    val message: List<Message>?,
)

data class Message(
    val id: String?,
    val message: String?,
    val timestamp: String?,
    val senderId: String?,
)