package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.model.GroupMemberItemEntity
import com.wd.woodong2.presentation.group.add.GroupAddSetItem
import com.wd.woodong2.presentation.group.detail.GroupDetailMemberAddItem
import com.wd.woodong2.presentation.group.detail.board.add.GroupDetailBoardAddItem
import com.wd.woodong2.presentation.group.detail.board.detail.GroupDetailBoardDetailItem
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun getGroupItems(): Flow<GroupItemsEntity>
    suspend fun setGroupItem(groupAddSetItem: List<GroupAddSetItem>): String
    fun setGroupBoardItem(itemId: String, groupBoardItem: GroupDetailBoardAddItem)
    fun setGroupAlbumItem(itemId: String, groupAlbumItems: List<String>)
    fun addGroupBoardComment(itemId: String, groupId: String, boardComment: GroupDetailBoardDetailItem.BoardComment, )
    fun deleteGroupBoardComment(itemId: String, boardId: String, commentId: String, )
    fun setGroupMemberItem(itemId: String, groupMemberItem: GroupDetailMemberAddItem)
    suspend fun getGroupItem(groupId: String): Flow<GroupItemsEntity?>
    suspend fun getGroupMemberList(groupId: String): Flow<List<GroupMemberItemEntity>?>
    fun deleteGroupBoardItem(itemId: String, boardId: String)
}