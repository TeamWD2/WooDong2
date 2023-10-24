package com.wd.woodong2.presentation.group.add

sealed class GroupAddSetItem(
    open val viewType: String,
) {
    data class GroupAddIntroduce(
        override val viewType: String = "introduce",
        val title: String?,
        val groupTag: String? = null,
        val groupName: String? = null,
        val introduce: String? = null,
        val ageLimit: String? = null,
        val memberLimit: String? = null,
        val password: String? = null,
        val mainImage: String? = null,
        val backgroundImage: String? = null,
        val timestamp: Long = System.currentTimeMillis()
    ) : GroupAddSetItem(viewType)

    data class GroupAddMember(
        override val viewType: String = "member",
        val title: String?,
        val memberList: List<AddMember>? = null
    ) : GroupAddSetItem(viewType)

    data class AddMember(
        val userId: String? = null,
        val profile: String? = null,
        val name: String? = null,
        val location: String? = null
    )
}