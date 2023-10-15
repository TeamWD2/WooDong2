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
    val lastMassage: String?,
    val message: List<Message>?,
)

data class Message(
    val id: String?,
    val massage: String?,
    val timestamp: String?,
    val senderId: String?,
)