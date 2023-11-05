package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.presentation.group.add.GroupAddSetItem
import com.wd.woodong2.presentation.group.detail.board.add.GroupDetailBoardAddItem
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun getGroupItems(): Flow<GroupItemsEntity>
    suspend fun setGroupItem(groupAddSetItem: List<GroupAddSetItem>)
    suspend fun setGroupBoardItem(itemId: String, groupBoardItem: List<GroupDetailBoardAddItem>)

    suspend fun setGroupAlbumItem(itemId: String, groupAlbumItems: List<String>)
}