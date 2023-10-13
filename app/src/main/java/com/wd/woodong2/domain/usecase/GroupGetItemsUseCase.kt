package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.repository.GroupRepository
import com.wd.woodong2.presentation.group.content.GroupItem

class GroupGetItemsUseCase(
    private val repository: GroupRepository
) {
    operator fun invoke(entityResult: (GroupItemsEntity?) -> Unit) {
        return repository.getGroupItems { result ->
            entityResult(result)
        }
    }
}