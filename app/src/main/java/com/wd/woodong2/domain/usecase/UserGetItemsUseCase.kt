package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserGetItemsUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Flow<UserEntity?> {
        return repository.getUser(userId)
    }
}