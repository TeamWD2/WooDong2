package com.wd.woodong2.presentation.group.content

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupItem(
    val id: String?,
    val title: String?,
    val introduce: String?,
    val groupTag: String?,
    val ageLimit: String?,
    val memberLimit: String?,
    val memberList: List<Member>?,
    val password: String?,
    val mainImage: String?,
    val backgroundImage: String?
): Parcelable

@Parcelize
data class Member(
    val userId: String,
    val userName: String,
    val userProfile: String
): Parcelable