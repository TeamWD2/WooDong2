package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.GroupRepository
import com.wd.woodong2.presentation.group.add.GroupAddSetItem

class GroupSetItemUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupAddSetItem: GroupAddSetItem) {
        return repository.setGroupItem(groupAddSetItem)
    }
}