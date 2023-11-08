package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.UserRepository

class UserUpdateGroupInfoUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, groupId: String, chatId: String) {
        return repository.updateGroupInfo(userId, groupId, chatId)
    }
}