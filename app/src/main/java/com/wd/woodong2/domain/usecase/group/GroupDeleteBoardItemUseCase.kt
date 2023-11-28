package com.wd.woodong2.domain.usecase.group

import com.wd.woodong2.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow

class GroupDeleteBoardItemUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(
        itemId: String,
        boardId: String
    ): Flow<List<String>?> {
        return repository.deleteGroupBoardItem(itemId, boardId)
    }
}