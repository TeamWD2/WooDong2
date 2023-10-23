package com.wd.woodong2.domain.model

import com.wd.woodong2.data.model.BoardItemResponse
import com.wd.woodong2.data.model.GroupBoardResponse
import com.wd.woodong2.data.model.GroupIntroduceResponse
import com.wd.woodong2.data.model.GroupItemsResponse
import com.wd.woodong2.data.model.GroupMemberResponse
import com.wd.woodong2.data.model.GroupResponse
import com.wd.woodong2.data.model.MemberItemResponse
import com.wd.woodong2.data.model.PhotoItemResponse

fun GroupItemsResponse.toEntity() = GroupItemsEntity(
    groupItems = groupItems.map {
        it.toEntity()
    }
)

fun GroupResponse.toEntity() = GroupEntity(
    id = id,
    introduce = introduce?.toEntity(),
    member = member?.toEntity(),
    board = board?.toEntity()
)

fun GroupIntroduceResponse.toEntity() = GroupIntroduceEntity(
    title = title,
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
    memberList = memberList?.map {
        it.toEntity()
    }
)

fun MemberItemResponse.toEntity() = MemberItemEntity(
    userId = userId,
    userName = userName,
    userProfile = userProfile
)

fun GroupBoardResponse.toEntity() = GroupBoardEntity(
    boardList = boardList?.map {
        it.toEntity()
    }
)

fun BoardItemResponse.toEntity() = BoardItemEntity(
    userId = userId,
    userName = userName,
    userProfile = userProfile,
    timestamp = timestamp,
    content = content,
    photoList = photoList?.map {
        it.toEntity()
    }
)

fun PhotoItemResponse.toEntity() = PhotoItemEntity(
    photo = photo
)