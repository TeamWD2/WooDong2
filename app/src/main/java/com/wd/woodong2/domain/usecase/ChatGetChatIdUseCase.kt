package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ChatGetChatIdUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(groupId: String): Flow<String> {
        return repository.getChatId(groupId)
    }
}