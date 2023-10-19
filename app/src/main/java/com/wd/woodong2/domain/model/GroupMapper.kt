package com.wd.woodong2.domain.model

import com.wd.woodong2.data.model.GroupItemsResponse
import com.wd.woodong2.data.model.GroupResponse

fun GroupItemsResponse.toEntity() = GroupItemsEntity(
    groupItems = groupItems?.map {
        it.toEntity()
    }
)

fun GroupResponse.toEntity() = GroupEntity(
    id = id,
    title = title,
    introduce = introduce,
    groupTag = groupTag,
    ageLimit = ageLimit,
    memberLimit = memberLimit,
    password = password,
    mainImage = mainImage,
    backgroundImage = backgroundImage
)