package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.ChatItemsEntity
import com.wd.woodong2.domain.repository.ChatRepository

class ChatGetItemsUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(entityResult: (ChatItemsEntity?) -> Unit) {
        return repository.getChatItems { result ->
            entityResult(result)
        }
    }
}