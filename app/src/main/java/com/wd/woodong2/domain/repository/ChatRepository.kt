package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.ChatItemsEntity
import com.wd.woodong2.domain.model.MessageItemsEntity
import com.wd.woodong2.presentation.group.detail.GroupDetailChatItem
import kotlinx.coroutines.flow.Flow


interface ChatRepository {
    suspend fun loadChatItems(chatIds: List<String>): Flow<ChatItemsEntity?>
    suspend fun setChatItem(chatItem: GroupDetailChatItem): String
    suspend fun addChatMessageItem(userId: String, message: String, nickname: String)
    suspend fun loadMessageItems(chatId: String): Flow<MessageItemsEntity?>
    fun initChatItemTimestamp(chatId: String)
}