package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.ChatItemsEntity
import kotlinx.coroutines.flow.Flow


interface ChatRepository {
    suspend fun getChatItems(chatIds: List<String>): Flow<ChatItemsEntity?>
    suspend fun addChatItem(
        senderId: String,
        imgProfile: String,
        location: String,
    )
}