package com.wd.woodong2.domain.usecase.user

import com.wd.woodong2.domain.repository.UserRepository

class UserDeleteUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String) {
        return repository.deleteUser(userId)
    }
}