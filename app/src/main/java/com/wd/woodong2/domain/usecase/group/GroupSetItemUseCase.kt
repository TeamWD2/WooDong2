package com.wd.woodong2.domain.usecase.group

import com.wd.woodong2.domain.repository.GroupRepository
import com.wd.woodong2.presentation.group.add.GroupAddSetItem

class GroupSetItemUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupAddSetItem: List<GroupAddSetItem>): String {
        return repository.setGroupItem(groupAddSetItem)
    }
}