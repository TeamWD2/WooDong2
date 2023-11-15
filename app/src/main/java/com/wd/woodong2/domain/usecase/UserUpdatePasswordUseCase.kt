package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserUpdatePasswordUseCase (
    private val repository: UserRepository
) {
    suspend operator fun invoke(email: String, currentPassword: String, newPassword: String){
        return repository.updateUserPassword(email, currentPassword, newPassword)
    }
}