package com.wd.woodong2.domain.model


data class ChatItemsEntity(
    val chatItems: List<ChatEntity>?,
)

data class ChatEntity(
    val id: String?,
    val groupId: String?,
    val last: MessageEntity?,
    val mainImage: String?,
    val memberLimit: String?,
    val message: List<MessageEntity>?,
    val title: String?,
    val lastSeemTime: Map<String?, Long>?,
)