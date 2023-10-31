package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.UserPreferencesRepository

class SignInSaveUserUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    fun invoke(user: String, isLoggedIn: Boolean) {
        userPreferencesRepository.saveUser(user, isLoggedIn)
    }
}


