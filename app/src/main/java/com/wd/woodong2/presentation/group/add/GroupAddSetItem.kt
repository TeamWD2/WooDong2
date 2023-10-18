package com.wd.woodong2.presentation.group.add

data class GroupAddSetItem(
    val groupTag: String,
    val title: String,
    val introduce: String,
    val ageLimit: String,
    val memberLimit: String,
    val password: String,
    val mainImage: String,
    val backgroundImage: String
)