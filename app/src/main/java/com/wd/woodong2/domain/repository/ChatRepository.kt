package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.ChatItemsEntity


interface ChatRepository {
    fun getChatItems(chatIds: List<String>, entityResult: (ChatItemsEntity?) -> Unit)
}