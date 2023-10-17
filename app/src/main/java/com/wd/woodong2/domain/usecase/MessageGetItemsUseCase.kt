package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.MessageItemsEntity
import com.wd.woodong2.domain.repository.MessageRepository


class MessageGetItemsUseCase(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(chatId: String): MessageItemsEntity {
        return repository.getMessageItems(chatId)
    }
}