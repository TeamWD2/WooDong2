package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.MessageRepository

class MessageSendItemsUseCase(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(isGroup: Boolean, chatId: String, userId: String, message: String) {
        repository.addMessageItem(isGroup, chatId, userId, message)
    }
}