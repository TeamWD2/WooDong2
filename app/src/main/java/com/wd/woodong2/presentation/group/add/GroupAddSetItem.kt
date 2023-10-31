package com.wd.woodong2.presentation.group.add

sealed class GroupAddSetItem(
    open val viewType: String,
) {
    data class GroupAddMain(
        override val viewType: String = "main",
        val groupName: String?,
        val groupTag: String?,
        val ageLimit: String?,
        val memberLimit: String?,
        val password: String?,
        val mainImage: String?,
        val backgroundImage: String?,
        val memberCount: Int = 1,
        val boardCount: Int = 0
    ) : GroupAddSetItem(viewType)

    data class GroupAddIntroduce(
        override val viewType: String = "introduce",
        val title: String?,
        val introduce: String?,
        val groupTag: String?,
        val ageLimit: String?,
        val memberLimit: String?,
    ) : GroupAddSetItem(viewType)

    data class GroupAddMember(
        override val viewType: String = "member",
        val title: String?,
        val memberList: List<AddMember>?
    ) : GroupAddSetItem(viewType)

    data class AddMember(
        val userId: String?,
        val profile: String?,
        val name: String?,
        val location: String?
    )
}