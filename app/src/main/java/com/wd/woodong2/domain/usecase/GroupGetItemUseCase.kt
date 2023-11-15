package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow

class GroupGetItemUseCase(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(
        groupId: String
    ): Flow<GroupItemsEntity?> {
        return repository.getGroupItem(groupId)
    }
}