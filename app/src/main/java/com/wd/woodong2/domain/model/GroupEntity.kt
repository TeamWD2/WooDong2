package com.wd.woodong2.domain.model

data class GroupItemsEntity(
    val groupItems: List<GroupEntity>?
)

data class GroupEntity(
    val id: String?,
    val groupProfile: String?,
    val title: String?,
    val memberProfile1: String?,
    val memberProfile2: String?,
    val memberProfile3: String?,
    val memberCount: Int?,
    val tagLocation: String?,
    val tagCategory: String?,
    val tagCapacity: Int?,
)