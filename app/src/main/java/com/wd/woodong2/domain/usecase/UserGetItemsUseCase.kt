package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.UserItemsEntity
import com.wd.woodong2.domain.repository.UserRepository

class UserGetItemsUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(userId: String, entityResult: (UserItemsEntity?) -> Unit) {
        return repository.getUserItems(userId) { result ->
            entityResult(result)
        }
    }
}