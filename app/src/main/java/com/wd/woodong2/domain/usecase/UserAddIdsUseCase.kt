package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserAddIdsUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(userId: String,writtenId: String?, likedId: String?){
        return repository.addUserIds(userId, writtenId, likedId)
    }
}