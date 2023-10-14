package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName

data class UserItemsResponse(
    val groupItems: List<UserResponse>?
)

data class UserResponse(
    val id: String?,
    @SerializedName("name") val name: String?,
)