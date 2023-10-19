package com.wd.woodong2.presentation.home.add

data class FirebaseHomeItem(
    val title: String,
    val description: String,
    val thumbnail: String? = null,
    val tag : String? = null
)
