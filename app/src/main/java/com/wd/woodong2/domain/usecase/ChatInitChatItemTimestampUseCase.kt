package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.ChatRepository

class ChatInitChatItemTimestampUseCase(
    private val repository: ChatRepository,
) {
    operator fun invoke(chatId: String) {
        repository.initChatItemTimestamp(chatId)
    }
}