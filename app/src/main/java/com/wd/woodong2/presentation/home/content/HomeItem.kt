package com.wd.woodong2.presentation.home.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.net.URL

@Parcelize
data class HomeItem (
    val id: Long?,
    val tag: String?,
    val groupTag: String?,
    val thumbnail: String?,
    val thumbnailCount: Int = 0,
    val title: String?,
    val description: String?,
    val location: String?,
    val timeStamp: String?,
    val view: String?,
    val thumbCount: Int = 0,
    val chatCount: Int = 0
) : Parcelable {
    constructor() : this(
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
        0,
        0
    )
}