package com.wd.woodong2.presentation.home.add

data class HomeAddItem(
    val id: String? = null,
    val userId: String = "",
    val name: String = "",
    val tag : String? = null,
    val groupTag: String = "",
    val thumbnail: String? = null,
    val thumbnailCount: Int = 0,
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val timeStamp: Long? = System.currentTimeMillis(),
)