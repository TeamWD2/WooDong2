package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.repository.GroupRepository

class GroupGetItemsUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(): GroupItemsEntity {
        return repository.getGroupItems()
    }
}