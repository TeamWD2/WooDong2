package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.MessageItemsEntity

interface MessageRepositoryTest {
    fun getMessageItems(chatId: String, entityResult: (MessageItemsEntity?) -> Unit)
}