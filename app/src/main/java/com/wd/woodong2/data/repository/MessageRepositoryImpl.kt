package com.wd.woodong2.data.repository

import com.google.firebase.database.DatabaseReference
import com.google.gson.GsonBuilder
import com.wd.woodong2.data.model.ChatResponse
import com.wd.woodong2.data.model.MessageItemsResponse
import com.wd.woodong2.domain.model.MessageItemsEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.repository.MessageRepository
import kotlinx.coroutines.tasks.await

class MessageRepositoryImpl(
    private val databaseReference: DatabaseReference
) : MessageRepository {
    override suspend fun getMessageItems(chatId: String): MessageItemsEntity {
        val snapshot = databaseReference.get().await()
        val gson = GsonBuilder().create()

        val chatResponses = snapshot.children.mapNotNull { childSnapshot ->
            val jsonString = gson.toJson(childSnapshot.value)
            val response = gson.fromJson(jsonString, ChatResponse::class.java)
            response.copy(id = childSnapshot.key)
        }

        val messageResponses = chatResponses.find { chatResponse ->
            chatResponse.id == chatId
        }?.message ?: emptyList()

        return MessageItemsResponse(messageResponses).toEntity()
    }
}

