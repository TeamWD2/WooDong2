package com.wd.woodong2.domain.usecase.group

import com.wd.woodong2.domain.repository.GroupRepository
import com.wd.woodong2.presentation.group.detail.GroupDetailMemberAddItem
import com.wd.woodong2.presentation.group.detail.board.add.GroupDetailBoardAddItem

class GroupSetMemberItemUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(itemId: String, groupMemberItem: GroupDetailMemberAddItem) {
        return repository.setGroupMemberItem(itemId, groupMemberItem)
    }
}