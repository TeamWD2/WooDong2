package com.wd.woodong2.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import com.wd.woodong2.R
import com.wd.woodong2.WooDongApp
import com.wd.woodong2.data.model.ChatItemsResponse
import com.wd.woodong2.data.model.ChatResponse
import com.wd.woodong2.data.model.MessageItemsResponse
import com.wd.woodong2.data.model.MessageResponse
import com.wd.woodong2.domain.model.ChatItemsEntity
import com.wd.woodong2.domain.model.Message
import com.wd.woodong2.domain.model.MessageItemsEntity
import com.wd.woodong2.domain.model.toEntity
import com.wd.woodong2.domain.repository.ChatRepository
import com.wd.woodong2.data.model.GCMRequest
import com.wd.woodong2.retrofit.GCMRetrofitClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ChatRepositoryImpl(
    private val databaseReference: DatabaseReference,
) : ChatRepository {


    companion object {
        const val TAG: String = "ChatRepositoryImpl"

        const val pageSize: Int = 20
        var lastTimestamps: MutableMap<String, Long> = mutableMapOf()
    }

    /*
    * "chat_list"
    * */
    override suspend fun loadChatItems(chatIds: List<String>): Flow<ChatItemsEntity?> =
        callbackFlow {
            val listener = databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val gson = GsonBuilder().create()

                        val groupChatResponses =
                            snapshot.child("group").children.mapNotNull { childSnapshot ->
                                val jsonString = gson.toJson(childSnapshot.value)
                                val response = gson.fromJson(jsonString, ChatResponse::class.java)
                                response.copy(id = childSnapshot.key)
                            }

                        val privateChatResponses =
                            snapshot.child("private").children.mapNotNull { childSnapshot ->
                                val jsonString = gson.toJson(childSnapshot.value)
                                val response = gson.fromJson(jsonString, ChatResponse::class.java)
                                response.copy(id = childSnapshot.key)
                            }

                        val filteredChatResponses = groupChatResponses.filter {
                            it.id in chatIds
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
//        val currentTimeMillis = System.currentTimeMillis()
//
//        val chatRef = databaseReference.push()
//
//        val chatEntity = ChatEntity(
//            id = chatRef.key,
//            groupId = imgProfile,
//            last = senderId,
//            mainImage = location,
//            memberLimit = currentTimeMillis,
//            title = "",
//            message = emptyMap()
//        )
//
//        chatRef.setValue(chatEntity)
    }

    override suspend fun loadMessageItems(chatId: String): Flow<MessageItemsEntity?> = callbackFlow {
        val sortedDatabaseRef = databaseReference.child("message").orderByChild("timestamp")

        val lastTimestamp = lastTimestamps[chatId]

        val databaseRef = if (lastTimestamp == null) {
            sortedDatabaseRef.limitToLast(pageSize)
        } else {
            sortedDatabaseRef.endAt((lastTimestamp - 1).toDouble()).limitToLast(pageSize)
        }

        val listener = databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val gson = GsonBuilder().create()

                    val messageResponses =
                        snapshot.children.mapNotNull { childSnapshot ->
                            val jsonString = gson.toJson(childSnapshot.value)
                            val response =
                                gson.fromJson(jsonString, MessageResponse::class.java)
                            response.copy(id = childSnapshot.key)
                        }

                    lastTimestamps[chatId] = messageResponses.firstOrNull()?.timestamp ?: return

                    val entity = MessageItemsResponse(messageResponses).toEntity()
                    trySend(entity)
                } else {
                    // 없으면 빈 리스트 반환
                    trySend(MessageItemsEntity(emptyList()))
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

    override suspend fun addChatMessageItem(userId: String, message: String, nickname: String) {

        val messageRef = databaseReference.child("message").push()

        val messageData = Message(
            content = message,
            timestamp = System.currentTimeMillis(),
            senderId = userId,
            nickname = nickname,
        )

        // message 추가
        messageRef.setValue(messageData)

        // lastMessage 업데이트
        databaseReference.child("last").setValue(messageData)

        // FCM Notification 객체 생성
        // to -> 받는 사람 Token
        val notification = GCMRequest(
            to = WooDongApp.getApp().getString(R.string.test_client_token),
            data = mapOf("action" to "ChatDetail"),
            notification = mapOf(
                "title" to "GCM Test : title",
                "body" to "GCM Test : body"
            ),
        )

        try {
            val response = GCMRetrofitClient.gcmRemoteSource.sendNotification(notification)

            if (response.isSuccessful) {
                Log.d(TAG, "Notification sent successfully. ${response.body()?.string()}")
            } else {
                Log.e(
                    TAG,
                    "Failed to send notification: ${response.code()} ${response.message()}"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during network request.", e)
        }
    }
}

