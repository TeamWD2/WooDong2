package com.wd.woodong2.domain.usecase.user

import com.wd.woodong2.domain.repository.UserRepository

class UserRemoveIdsUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(userId : String, writtenId: String?, likedId: String?, groupId: String?, chatId: String?){
        return repository.removeUserIds(userId, writtenId, likedId, groupId, chatId)
    }
}