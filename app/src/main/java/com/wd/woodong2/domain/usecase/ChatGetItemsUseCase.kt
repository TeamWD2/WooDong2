package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.ChatItemsEntity
import com.wd.woodong2.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ChatGetItemsUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatIds: List<String>): Flow<ChatItemsEntity?> {
        return repository.loadChatItems(chatIds)
    }
}