package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.presentation.group.add.GroupAddSetItem
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun getGroupItems(): Flow<GroupItemsEntity>
    suspend fun setGroupItem(groupAddSetItem: GroupAddSetItem)
}