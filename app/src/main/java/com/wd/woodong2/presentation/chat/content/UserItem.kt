package com.wd.woodong2.presentation.chat.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserItem(
    val id: String?,
    val name: String?,
    val imgProfile: String?,
    val email: String?,
    val chatIds: List<String>?,
) : Parcelable
