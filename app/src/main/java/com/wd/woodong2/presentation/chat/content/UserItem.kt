package com.wd.woodong2.presentation.chat.content

import android.os.Parcelable
import com.wd.woodong2.presentation.home.content.HomeItem
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserItem(
    val id: String?,
    var name: String?,
    var imgProfile: String?,
    var email: String?,
    val chatIds: List<String>?,
    val firstLocation: String?,
    val secondLocation: String?
) : Parcelable