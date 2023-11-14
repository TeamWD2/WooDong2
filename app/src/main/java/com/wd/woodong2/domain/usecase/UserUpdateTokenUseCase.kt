package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.UserRepository

class UserUpdateTokenUseCase(
    private val repository: UserRepository,
) {
    operator fun invoke(userId: String) {
        return repository.updateUserToken(userId)
    }
}