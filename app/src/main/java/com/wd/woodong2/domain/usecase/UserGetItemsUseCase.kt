package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserGetItemsUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Flow<UserEntity?> {
        return repository.getUser(userId)
    }
    operator fun invoke(userId: String, firstLocation: String, secondLocation: String) {
        return repository.updateUserLocations(userId, firstLocation, secondLocation)
    }
    operator fun invoke(userId: String, name: String, imgProfile: String, email: String){
        return repository.updateUserInfo(userId, name, imgProfile, email)
    }

}