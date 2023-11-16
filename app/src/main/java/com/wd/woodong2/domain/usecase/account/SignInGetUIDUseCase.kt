package com.wd.woodong2.domain.usecase.account

import com.wd.woodong2.domain.repository.UserPreferencesRepository

class SignInGetUIDUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    operator fun invoke(): String? {
        return userPreferencesRepository.getUID()
    }
}