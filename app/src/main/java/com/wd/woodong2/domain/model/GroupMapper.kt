package com.wd.woodong2.domain.model

import com.wd.woodong2.data.model.GroupAlbumResponse
import com.wd.woodong2.data.model.GroupBoardItemResponse
import com.wd.woodong2.data.model.GroupBoardResponse
import com.wd.woodong2.data.model.GroupIntroduceResponse
import com.wd.woodong2.data.model.GroupItemsResponse
import com.wd.woodong2.data.model.GroupMemberItemResponse
import com.wd.woodong2.data.model.GroupMemberResponse

fun GroupItemsResponse.toEntity() = GroupItemsEntity(
    groupList = groupList.map {
        when(it) {
            is GroupIntroduceResponse -> it.toEntity()
            is GroupMemberResponse -> it.toEntity()
            is GroupBoardResponse -> it.toEntity()
            is GroupAlbumResponse -> it.toEntity()
        }
    }
)

fun GroupIntroduceResponse.toEntity() = GroupIntroduceEntity(
    title = title,
    groupName = groupName,
    introduce = introduce,
    groupTag = groupTag,
    ageLimit = ageLimit,
    memberLimit = memberLimit,
    password = password,
    mainImage = mainImage,
    backgroundImage = backgroundImage,
    timestamp = timestamp
)

fun GroupMemberResponse.toEntity() = GroupMemberEntity(
    title = title,
    memberList = memberList?.map {
        it.toEntity()
    }
)

fun GroupMemberItemResponse.toEntity() = GroupMemberItemEntity(
    userId = userId,
    profile = profile,
    name = name,
    location = location
)

fun GroupBoardResponse.toEntity() = GroupBoardEntity(
    title = title,
    boardList = boardList?.map {
        it.toEntity()
    }
)

fun GroupBoardItemResponse.toEntity() = GroupBoardItemEntity(
    userId = userId,
    profile = profile,
    name = name,
    location = location,
    timestamp = timestamp,
    content = content,
    images = images
)

fun GroupAlbumResponse.toEntity() = GroupAlbumEntity(
    title = title,
    images = images
)