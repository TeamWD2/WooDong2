package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.MessageRepository

class MessageSendItemsUseCase(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(chatId: String, userId: String, message: String) {
        repository.addMessageItem(chatId, userId, message)
    }
}