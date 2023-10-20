package com.wd.woodong2.presentation.home.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeItem (
    val id: String?,
    val name: String?,
    val tag: String?,
    val groupTag: String?,
    val thumbnail: String?,
    val thumbnailCount: Int?,
    val title: String?,
    val description: String?,
    val firstLocation: String?,
    val secondLocation: String?,
    val timeStamp: String?,
    val view: String?,
    val thumbCount: Int ,
    val chatCount: Int
) : Parcelable{
    constructor(id: String?, name: String?, firstLocation: String?, secondLocation: String?) : this(
        null,
        null,
        null,
        null,
        null,
        0,
        null,
        null,
        null,
        null,
        null,
        null,
        0,
        0
    )
}
//작성자도 필요 할 거 같다.


//fun HomeItem.toUserItem(): UserItem{
//    return UserItem(
//
//    )
//}
