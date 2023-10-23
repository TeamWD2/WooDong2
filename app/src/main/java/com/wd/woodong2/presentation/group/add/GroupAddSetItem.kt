package com.wd.woodong2.presentation.group.add

data class GroupAddSetItem(
    val introduce: GroupAddIntroduce? = null,
    val member: GroupAddMember? = null,
)

data class GroupAddIntroduce(
    val groupTag: String? = null,
    val title: String? = null,
    val introduce: String? = null,
    val ageLimit: String? = null,
    val memberLimit: String? = null,
    val password: String? = null,
    val mainImage: String? = null,
    val backgroundImage: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class GroupAddMember(
    val memberList: List<Member>? = null
)

data class Member(
    val userId: String? = null,
    val userProfile: String? = null,
    val userName: String? = null
)