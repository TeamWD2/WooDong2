package com.wd.woodong2.presentation.home.content

import android.os.Parcelable
import com.wd.woodong2.presentation.home.detail.CommentItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeItem(
    val id: String = "",
    val userId : String = "",
    val name: String = "",
    val tag: String = "",
    val groupTag: String = "",
    val thumbnail: String = "",
    val thumbnailCount: Int = 0,
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val timeStamp: Long? = System.currentTimeMillis(),
    val view: String = "",
    var thumbCount: Int = 0,
    var chatCount: Int = 0,
    var isLiked: Boolean = false,
    var comments: MutableList<CommentItem> = mutableListOf(),
    val likedBy: MutableList<String> = mutableListOf()
) : Parcelable

