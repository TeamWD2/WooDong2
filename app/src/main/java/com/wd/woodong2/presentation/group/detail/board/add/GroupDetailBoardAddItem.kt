package com.wd.woodong2.presentation.group.detail.board.add

data class GroupDetailBoardAddItem (
    val userId: String,
    val profile: String?,
    val name: String,
    val location: String,
    val timestamp: Long = System.currentTimeMillis(),
    val content: String,
    val images: List<String>
)