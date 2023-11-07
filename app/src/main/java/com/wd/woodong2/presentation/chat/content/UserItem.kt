package com.wd.woodong2.presentation.chat.content

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.wd.woodong2.presentation.home.content.HomeItem
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserItem(
    val id: String?,
    val name: String?,
    val imgProfile: String?,
    val email: String?,
    val chatIds: List<String>?,
    val groupIds: List<String>?,        //모임
    val likedIds: List<String>?,        //좋아요 게시물
    val writtenIds: List<String>?,        //작성한 게시물
    val firstLocation: String?,
    val secondLocation: String?
) : Parcelable

