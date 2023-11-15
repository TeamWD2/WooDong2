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
    last = last?.toEntity(),
    mainImage = mainImage,
    memberLimit = memberLimit,
    message = message?.map { it.value.toEntity() }.orEmpty(),
    title = title,
    lastSeemTime = lastSeemTime
)