package com.wd.woodong2.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.wd.woodong2.data.model.ChatResponse
import com.wd.woodong2.data.model.MessageItemsResponse
import com.wd.woodong2.domain.model.Message
import com.wd.woodong2.domain.model.MessageItemsEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.repository.MessageRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MessageRepositoryImpl(
    private val databaseReference: DatabaseReference
) : MessageRepository {

    /*
    * databaseReference => chats
    */
    override suspend fun getMessageItems(chatId: String): Flow<MessageItemsEntity?> = callbackFlow {
        val listener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gson = GsonBuilder().create()

                    val chatResponses = snapshot.children.mapNotNull { childSnapshot ->
                        val jsonString = gson.toJson(childSnapshot.value)
                        val response = gson.fromJson(jsonString, ChatResponse::class.java)
                        response.copy(id = childSnapshot.key)
                    }

                    val messageResponses = chatResponses.find { chatResponse ->
                        chatResponse.id == chatId
                    }?.message

                    val entity = MessageItemsResponse(messageResponses).toEntity()
                    trySend(entity)
                } else {
                    throw RuntimeException("snapshot is not exists")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
        awaitClose {
            databaseReference.removeEventListener(listener)
        }
    }

    override suspend fun addMessageItem(chatId: String, userId: String, message: String) {
        val chatRef = databaseReference.child(chatId)
        val messageRef = chatRef.child("message").push()

        val currentTimeMillis = System.currentTimeMillis()

        val messageData = Message(
            messageRef.key,
            message,
            currentTimeMillis,
            userId
        )

        messageRef.setValue(messageData)
        chatRef.child("lastMessage").setValue(message)
        chatRef.child("timestamp").setValue(currentTimeMillis)
    }
}

