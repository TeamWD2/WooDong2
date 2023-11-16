package com.wd.woodong2.domain.usecase.chat

import com.wd.woodong2.domain.repository.ChatRepository

class ChatSendMessageUseCase(
    private val repository: ChatRepository,
) {
    suspend operator fun invoke(
        userId: String,
        message: String,
        nickname: String,
        profileImg: String,
    ) {
        repository.addChatMessageItem(userId, message, nickname, profileImg)
    }
}