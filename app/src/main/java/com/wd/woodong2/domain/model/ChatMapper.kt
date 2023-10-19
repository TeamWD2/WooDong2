package com.wd.woodong2.domain.model

import com.wd.woodong2.data.model.ChatItemsResponse
import com.wd.woodong2.data.model.ChatResponse
import com.wd.woodong2.data.model.LastResponse

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
    message = message?.mapValues { it.value.toMessage() }.orEmpty(),
    title = title,
)