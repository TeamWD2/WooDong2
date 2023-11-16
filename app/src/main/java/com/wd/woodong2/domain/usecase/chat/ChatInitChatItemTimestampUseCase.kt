package com.wd.woodong2.domain.usecase.chat

import com.wd.woodong2.domain.repository.ChatRepository

class ChatInitChatItemTimestampUseCase(
    private val repository: ChatRepository,
) {
    operator fun invoke(chatId: String, userId: String) {
        repository.initChatItemTimestamp(chatId, userId)
    }
}