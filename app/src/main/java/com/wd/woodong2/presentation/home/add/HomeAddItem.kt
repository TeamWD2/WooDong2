package com.wd.woodong2.presentation.home.add

data class HomeAddItem(
    val title: String,
    val description: String,
    val thumbnail: String? = null,
    val tag : String? = null
)
