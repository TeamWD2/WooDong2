package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserSignInUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: String, password: String): Flow<Boolean> {
        return repository.signIn(id, password)
    }
}