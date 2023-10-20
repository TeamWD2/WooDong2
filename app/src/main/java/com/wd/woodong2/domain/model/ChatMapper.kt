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
    groupId = groupId,
    last = last?.toMessage(),
    mainImage = mainImage,
    memberLimit = memberLimit,
    message = message?.map { it.value.toMessage() }.orEmpty(),
    title = title,
)