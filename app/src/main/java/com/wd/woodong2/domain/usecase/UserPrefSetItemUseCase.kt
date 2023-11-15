package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.repository.UserPreferencesRepository

class UserPrefSetItemUseCase(
    private val repository: UserPreferencesRepository,
) {
    operator fun invoke(user: UserEntity) {
        return repository.setUserInfo(user)
    }
}