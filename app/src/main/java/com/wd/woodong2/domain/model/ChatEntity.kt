package com.wd.woodong2.domain.model


data class ChatItemsEntity(
    val chatItems: List<ChatEntity>?
)

data class ChatEntity(
    val id: String?,
    val imgProfile: String?,
    val sender: String?,
    val location: String?,
    val timestamp: String?,
    val lastMassage: String?,
)