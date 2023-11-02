package com.wd.woodong2.domain.model

import com.wd.woodong2.data.model.UserItemsResponse
import com.wd.woodong2.data.model.UserResponse


fun UserItemsResponse.toEntity() = UserItemsEntity(
    userItems = groupItems?.map {
        it.toEntity()
    }
)

fun UserResponse.toEntity() = UserEntity(
    id = id,
    name = name,
    imgProfile = imgProfile,
    email = email,
    chatIds = chatIds?.values?.map { it }.orEmpty(),
    firstLocation = firstLocation,
    secondLocation = secondLocation,
    token = token
)