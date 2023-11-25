package com.wd.woodong2.domain.usecase.group

import com.wd.woodong2.domain.repository.GroupRepository
import com.wd.woodong2.presentation.group.detail.board.detail.GroupDetailBoardDetailItem

class GroupDeleteBoardItemUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(
        itemId: String,
        groupId: String
    ) {
        return repository.deleteGroupBoardItem(itemId, groupId)
    }
}