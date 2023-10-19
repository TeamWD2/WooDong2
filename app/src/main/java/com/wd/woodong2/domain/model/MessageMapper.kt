package com.wd.woodong2.domain.model

import com.wd.woodong2.data.model.MessageItemsResponse
import com.wd.woodong2.data.model.MessageResponse

fun MessageItemsResponse.toEntity() = MessageItemsEntity(
    messageItems = messageItems?.map {
        it.toEntity()
    }
)

fun MessageResponse.toEntity() = MessageEntity(
    id = id,
    senderId = senderId,
    timestamp = timestamp,
    content = content,
)

fun MessageResponse.toMessage() = Message(
    senderId = senderId,
    timestamp = timestamp,
    content = content,
)