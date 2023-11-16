package com.wd.woodong2.domain.usecase.account

import com.wd.woodong2.domain.repository.UserPreferencesRepository

class SignInSaveUserUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    operator fun invoke(user: String, isLoggedIn: Boolean, uid: String) {
        userPreferencesRepository.saveUser(user, isLoggedIn, uid)
    }
}


