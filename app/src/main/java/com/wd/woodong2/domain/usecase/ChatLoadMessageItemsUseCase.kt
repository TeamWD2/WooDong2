package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.MessageItemsEntity
import com.wd.woodong2.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow


class ChatLoadMessageItemsUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: String): Flow<MessageItemsEntity?> {
        return repository.loadMessageItems(chatId)
    }
}