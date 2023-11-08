package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.ChatRepository
import com.wd.woodong2.presentation.group.detail.GroupDetailChatItem
import kotlinx.coroutines.flow.Flow

class ChatSetItemUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatItem: GroupDetailChatItem): String {
        return repository.setChatItem(chatItem)
    }
}