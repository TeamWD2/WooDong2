package com.wd.woodong2.domain.usecase.group

import com.wd.woodong2.domain.repository.GroupRepository
import com.wd.woodong2.presentation.group.detail.board.add.GroupDetailBoardAddItem

class GroupSetBoardItemUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(itemId: String, groupBoardItem: GroupDetailBoardAddItem) {
        return repository.setGroupBoardItem(itemId, groupBoardItem)
    }
}