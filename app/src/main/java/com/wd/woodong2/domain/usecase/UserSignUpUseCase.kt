package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserSignUpUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(id: String, password: String, name: String): Flow<Boolean> {
        return repository.signUp(id, password, name)
    }
}