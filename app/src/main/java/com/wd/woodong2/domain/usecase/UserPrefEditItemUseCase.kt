package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.repository.UserPreferencesRepository

class UserPrefEditItemUseCase (
    private val repository: UserPreferencesRepository,
 ){
    operator fun invoke(name: String?, profile: String?,firstLocation: String?, secondLocation: String?): UserEntity? {
        return repository.editUserInfo(name, profile,firstLocation, secondLocation)
    }
}