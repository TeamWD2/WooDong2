package com.wd.woodong2.domain.model

import com.wd.woodong2.data.model.GroupAlbumResponse
import com.wd.woodong2.data.model.GroupBoardCommentResponse
import com.wd.woodong2.data.model.GroupBoardItemResponse
import com.wd.woodong2.data.model.GroupBoardResponse
import com.wd.woodong2.data.model.GroupIntroduceResponse
import com.wd.woodong2.data.model.GroupItemsResponse
import com.wd.woodong2.data.model.GroupMainResponse
import com.wd.woodong2.data.model.GroupMemberItemResponse
import com.wd.woodong2.data.model.GroupMemberResponse

fun GroupItemsResponse.toEntity() = GroupItemsEntity(
    groupList = groupList.map {
        when (it) {
            is GroupMainResponse -> it.toEntity()
            is GroupIntroduceResponse -> it.toEntity()
            is GroupMemberResponse -> it.toEntity()
            is GroupBoardResponse -> it.toEntity()
            is GroupAlbumResponse -> it.toEntity()
        }
    }
)

fun GroupMainResponse.toEntity() = GroupMainEntity(
    id = id,
    groupName = groupName,
    introduce = introduce,
    groupTag = groupTag,
    ageLimit = ageLimit,
    memberLimit = memberLimit,
    password = password,
    mainImage = mainImage,
    backgroundImage = backgroundImage
)

fun GroupIntroduceResponse.toEntity() = GroupIntroduceEntity(
    id = id,
    title = title,
    introduce = introduce,
    groupTag = groupTag,
    ageLimit = ageLimit,
    memberLimit = memberLimit,
)

fun GroupMemberResponse.toEntity() = GroupMemberEntity(
    id = id,
    title = title,
    memberList = memberList?.values?.map {
        it.toEntity()
    }
)

fun GroupMemberItemResponse.toEntity() = GroupMemberItemEntity(
    userId = userId,
    profile = profile,
    name = name,
    location = location,
    comment = comment
)

fun GroupBoardResponse.toEntity() = GroupBoardEntity(
    id = id,
    title = title,
    boardList = boardList?.mapValues { (_, value) ->
        value.toEntity()
    }
)

fun GroupBoardItemResponse.toEntity() = GroupBoardItemEntity(
    userId = userId,
    profile = profile,
    name = name,
    location = location,
    timestamp = timestamp,
    content = content,
    images = images,
    commentList = commentList?.mapValues { (_, value) ->
        value.toEntity()
    }
)

fun GroupBoardCommentResponse.toEntity() = GroupBoardCommentEntity(
    userId = userId,
    userProfile = userProfile,
    userName = userName,
    userLocation = userLocation,
    timestamp = timestamp,
    comment = comment
)

fun GroupAlbumResponse.toEntity() = GroupAlbumEntity(
    id = id,
    title = title,
    images = images
)