package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserUpdateItemUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(userId: String): Flow<Boolean> {
        return repository.updateUserToken(userId)
    }
}