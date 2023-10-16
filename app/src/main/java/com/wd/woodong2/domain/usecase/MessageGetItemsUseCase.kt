package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.MessageItemsEntity
import com.wd.woodong2.domain.repository.MessageRepository


class MessageGetItemsUseCase(
    private val repository: MessageRepository
) {
    operator fun invoke(chatId: String, entityResult: (MessageItemsEntity?) -> Unit) {
        return repository.getMessageItems(chatId) { result ->
            entityResult(result)
        }
    }
}