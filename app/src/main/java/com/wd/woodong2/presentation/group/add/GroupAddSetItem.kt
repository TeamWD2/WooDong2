package com.wd.woodong2.presentation.group.add

sealed class GroupAddSetItem(
    open val viewType: String,
) {
    data class GroupAddMain(
        override val viewType: String = "main",
        val groupName: String? = null,
        val introduce: String? = null,
        val groupTag: String? = null,
        val ageLimit: String? = null,
        val memberLimit: String? = null,
        val password: String? = null,
        val mainImage: String? = null,
        val backgroundImage: String? = null,
        val memberCount: Int = 1,
        val boardCount: Int = 0
    ) : GroupAddSetItem(viewType)

    data class GroupAddIntroduce(
        override val viewType: String = "introduce",
        val title: String = "소개",
        val introduce: String? = null,
        val groupTag: String? = null,
        val ageLimit: String? = null,
        val memberLimit: String? = null
    ) : GroupAddSetItem(viewType)

    data class GroupAddMember(
        override val viewType: String = "member",
        val title: String = "멤버",
        val memberList: List<AddMember>?
    ) : GroupAddSetItem(viewType)

    data class AddMember(
        val userId: String?,
        val profile: String?,
        val name: String?,
        val location: String?,
        val comment: String?
    )

    data class GroupAddBoard(
        override val viewType: String = "board",
        val title: String = "게시판"
    ) : GroupAddSetItem(viewType)

    data class GroupAddAlbum(
        override val viewType: String = "album",
        val title: String = "앨범"
    ) : GroupAddSetItem(viewType)
}