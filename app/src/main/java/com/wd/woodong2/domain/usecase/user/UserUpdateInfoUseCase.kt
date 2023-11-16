package com.wd.woodong2.domain.usecase.user

import com.wd.woodong2.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserUpdateInfoUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(userId: String, imgProfile: String, name: String, firstLocation: String, secondLocation: String){
        return repository.updateUserInfo(userId, imgProfile, name, firstLocation, secondLocation)
    }
}