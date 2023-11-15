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
    groupIds= groupIds?.values?.map { it }.orEmpty(),        //모임
    likedIds= likedIds?.values?.map { it }.orEmpty(),        //좋아요 게시물
    writtenIds= writtenIds?.values?.map { it }.orEmpty(),        //작성한 게시물
    firstLocation = firstLocation,
    secondLocation = secondLocation,
    token = token
)