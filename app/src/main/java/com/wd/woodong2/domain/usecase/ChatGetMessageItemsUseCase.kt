package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.MessageItemsEntity
import com.wd.woodong2.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow


class ChatGetMessageItemsUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(): Flow<MessageItemsEntity?> {
        return repository.getMessageItems()
    }
}