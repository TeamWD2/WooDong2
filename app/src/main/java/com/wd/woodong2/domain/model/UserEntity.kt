package com.wd.woodong2.domain.model

import com.google.gson.annotations.SerializedName

data class UserItemsEntity(
    val userItems: List<UserEntity>?
)

data class UserEntity(
    val id: String?,
    val name: String?,
    val imgProfile: String?,
    val email: String?,
    val chatIds: List<String>?,
    val groupIds: List<String>?,        //모임
    val likedIds: List<String>?,        //좋아요 게시물
    val writtenIds: List<String>?,        //작성한 게시물
    val firstLocation: String?,
    val secondLocation: String?,
    val token: String?,
)