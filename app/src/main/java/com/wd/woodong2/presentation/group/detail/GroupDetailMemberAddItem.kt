package com.wd.woodong2.presentation.group.detail

data class GroupDetailMemberAddItem (
    val userId: String,
    val profile: String?,
    val name: String,
    val location: String,
    val comment: String?
)