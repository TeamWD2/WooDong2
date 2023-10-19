package com.wd.woodong2.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.wd.woodong2.data.model.ChatItemsResponse
import com.wd.woodong2.data.model.ChatResponse
import com.wd.woodong2.domain.model.ChatEntity
import com.wd.woodong2.domain.model.ChatItemsEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatRepositoryImpl(private val databaseReference: DatabaseReference) : ChatRepository {

    override suspend fun getChatItems(chatIds: List<String>): Flow<ChatItemsEntity?> =
        callbackFlow {
            val listener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        val gson = GsonBuilder().create()

                        val chatResponses = snapshot.children.mapNotNull { childSnapshot ->
                            val jsonString = gson.toJson(childSnapshot.value)
                            val response = gson.fromJson(jsonString, ChatResponse::class.java)
                            response.copy(id = childSnapshot.key)
                        }

                        val filteredChatResponses = chatResponses.filter { chatResponse ->
                            chatResponse.id in chatIds
                        }

                        val entity = ChatItemsResponse(filteredChatResponses).toEntity()
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

    override suspend fun addChatItem(
        senderId: String,
        imgProfile: String,
        location: String,
    ) {
        val currentTimeMillis = System.currentTimeMillis()

        val chatRef = databaseReference.push()

        val chatEntity = ChatEntity(
            id = chatRef.key,
            imgProfile = imgProfile,
            senderId = senderId,
            location = location,
            timestamp = currentTimeMillis,
            lastMessage = "",
            message = emptyMap()
        )

        chatRef.setValue(chatEntity)
    }
}

