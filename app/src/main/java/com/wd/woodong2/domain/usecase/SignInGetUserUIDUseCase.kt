package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.UserRepository

class SignInGetUserUIDUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(): String? {
        return repository.getUid()
    }
}