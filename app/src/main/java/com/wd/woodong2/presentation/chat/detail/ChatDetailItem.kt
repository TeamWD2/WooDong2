package com.wd.woodong2.presentation.chat.detail

import com.wd.woodong2.presentation.group.content.GroupItem

data class ChatDetailItem(
    /**
     * GroupMain
     * */
    val id: String? = "",
    val title: String? = "",
    val groupName: String? = "",
    val memberLimit: String? = "",
    val mainImage: String? = "",

    /**
     * GroupMember
     * */
    val memberList: List<GroupItem.Member>? = emptyList(),
)
