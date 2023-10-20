package com.wd.woodong2.presentation.group.add

import android.net.Uri

data class GroupAddSetItem(
    val groupTag: String? = null,
    val title: String? = null,
    val introduce: String? = null,
    val ageLimit: String? = null,
    val memberLimit: String? = null,
    val memberList: List<Member>? = null,
    val password: String? = null,
    val mainImage: Uri? = null,
    val backgroundImage: Uri? = null
)

data class Member(
    val userId: String?,
    val userProfile: String?,
    val userName: String?
)