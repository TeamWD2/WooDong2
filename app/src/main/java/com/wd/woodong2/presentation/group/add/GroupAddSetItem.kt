package com.wd.woodong2.presentation.group.add

data class GroupAddSetItem(
    val introduce: GroupAddIntroduce? = null,
    val member: List<GroupAddMember>? = null,
)

data class GroupAddIntroduce(
    val groupTag: String? = null,
    val title: String? = null,
    val introduce: String? = null,
    val ageLimit: String? = null,
    val memberLimit: String? = null,
    val password: String? = null,
    val mainImage: String? = null,
    val backgroundImage: String? = null
)

data class GroupAddMember(
    val userId: String?,
    val userProfile: String?,
    val userName: String?
)