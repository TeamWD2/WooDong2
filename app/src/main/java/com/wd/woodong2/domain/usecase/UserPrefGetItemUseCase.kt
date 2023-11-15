package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.repository.UserPreferencesRepository

class UserPrefGetItemUseCase(
    private val repository: UserPreferencesRepository,
) {
    operator fun invoke(): UserEntity? {
        return repository.getUserInfo()
    }
}