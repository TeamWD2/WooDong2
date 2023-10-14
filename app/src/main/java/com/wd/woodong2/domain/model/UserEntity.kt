package com.wd.woodong2.domain.model

data class UserItemsEntity(
    val userItems: List<UserEntity>?
)

data class UserEntity(
    val id: String?,
    val name: String?,
)