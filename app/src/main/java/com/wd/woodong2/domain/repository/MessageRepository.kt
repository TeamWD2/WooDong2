package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.MessageItemsEntity
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun getMessageItems(chatId: String): Flow<MessageItemsEntity?>
    suspend fun addMessageItem(isGroup: Boolean, chatId: String, userId: String, message: String)
}