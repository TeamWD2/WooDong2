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
    groupProfile = groupProfile,
    title = title,
    memberProfile1 = memberProfile1,
    memberProfile2 = memberProfile2,
    memberProfile3 = memberProfile3,
    memberCount = memberCount,
    tagLocation = tagLocation,
    tagCategory = tagCategory,
    tagCapacity = tagCapacity
)