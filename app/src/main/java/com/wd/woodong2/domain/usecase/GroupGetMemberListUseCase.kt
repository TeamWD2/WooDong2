package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.GroupMemberItemEntity
import com.wd.woodong2.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow

class GroupGetMemberListUseCase(
    private val repository: GroupRepository,
) {
    suspend operator fun invoke(
        groupId: String,
    ): Flow<List<GroupMemberItemEntity>?> {
        return repository.getGroupMemberList(groupId)
    }
}