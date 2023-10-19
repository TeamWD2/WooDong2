package com.wd.woodong2.domain.model

data class GroupItemsEntity(
    val groupItems: List<GroupEntity>?
)

data class GroupEntity(
    val id: String?,
    val title: String?,
    val introduce: String?,
    val groupTag: String?,
    val ageLimit: String?,
    val memberLimit: String?,
    val password: String?,
    val mainImage: String?,
    val backgroundImage: String?
)