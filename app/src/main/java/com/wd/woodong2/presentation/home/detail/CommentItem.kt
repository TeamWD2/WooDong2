package com.wd.woodong2.presentation.home.detail

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommentItem(
    val username: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val location: String = ""
) : Parcelable
