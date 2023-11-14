package com.wd.woodong2.presentation.group

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupUserInfoItem (
    val userId: String,
    val userProfile: String?,
    val userName: String,
    val userFirstLocation: String,
    val userSecondLocation: String,
) : Parcelable