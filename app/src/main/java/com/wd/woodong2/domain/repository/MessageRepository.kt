package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.MessageItemsEntity

interface MessageRepository {
    suspend fun getMessageItems(chatId: String): MessageItemsEntity
}