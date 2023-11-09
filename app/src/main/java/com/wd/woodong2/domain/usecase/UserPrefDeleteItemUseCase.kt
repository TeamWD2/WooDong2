package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.UserPreferencesRepository

class UserPrefDeleteItemUseCase(
    private val repository: UserPreferencesRepository,
) {
    operator fun invoke() {
        return repository.deleteUser()
    }
}