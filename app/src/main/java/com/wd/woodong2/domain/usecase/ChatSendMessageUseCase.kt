package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.ChatRepository

class ChatSendMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(userId: String, message: String, nickname: String) {
        repository.addChatMessageItem(userId, message, nickname)
    }
}