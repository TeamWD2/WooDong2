package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserAddIdsUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String,writtenId: String?, groupId: String?, likedId: String?): Flow<UserEntity?> {
        return repository.addUserIds(userId, writtenId, groupId, likedId)
    }
}