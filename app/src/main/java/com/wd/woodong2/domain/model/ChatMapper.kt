package com.wd.woodong2.domain.model

import com.wd.woodong2.data.model.ChatItemsResponse
import com.wd.woodong2.data.model.ChatResponse

fun ChatItemsResponse.toEntity() = ChatItemsEntity(
    chatItems = chatItems?.map {
        it.toEntity()
    }
)

fun ChatResponse.toEntity() = ChatEntity(
    id = id,
    imgProfile = imgProfile,
    senderId = senderId,
    location = location,
    timestamp = timestamp,
    lastMessage = lastMassage,
    message = message.values.toList().map { it.toMessage() }
)