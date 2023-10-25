package com.wd.woodong2.presentation.chat.content

import android.os.Parcelable
import com.wd.woodong2.presentation.home.content.HomeItem
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserItem(
    val id: String?,
    val name: String?,
    var imgProfile: String?,
    val email: String?,
    val chatIds: List<String>?,
    val firstLocation: String?,
    val secondLocation: String?
) : Parcelable
//fun UserItem.toHomeItem() : HomeItem{
//    return HomeItem(
//        id = id,
//        name = name,
//        firstLocation = firstLocation,
//        secondLocation = secondLocation,
//    )
//}