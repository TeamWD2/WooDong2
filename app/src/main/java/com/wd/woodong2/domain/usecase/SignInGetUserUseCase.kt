package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.UserPreferencesRepository

class SignInGetUserUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    fun invoke(): String? {
        return userPreferencesRepository.getUser()
    }
}