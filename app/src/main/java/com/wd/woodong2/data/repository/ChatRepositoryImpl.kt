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
import com.wd.woodong2.data.model.GroupItemsResponse
import com.wd.woodong2.data.model.GroupItemsResponseJsonDeserializer
import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.presentation.group.detail.GroupDetailChatItem
import com.wd.woodong2.retrofit.GCMRetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class ChatRepositoryImpl(
    private val chatDatabaseReference: DatabaseReference,
    private val timeDatabaseReference: DatabaseReference?,
) : ChatRepository {

    companion object {
        const val TAG: String = "ChatRepositoryImpl"

        const val pageSize: Int = 20
        var lastTimestamps: MutableMap<String, Long> = mutableMapOf()
        var lastSeemTime: MutableMap<String, Long> = mutableMapOf()
    }

    /*
    * "chat_list"
    * */
    override suspend fun loadChatItems(chatIds: List<String>): Flow<ChatItemsEntity?> =
        callbackFlow {
            val listener = chatDatabaseReference.addValueEventListener(object : ValueEventListener {
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
                chatDatabaseReference.removeEventListener(listener)
            }
        }

    override suspend fun setChatItem(chatItem: GroupDetailChatItem): String {
        val chatRef = chatDatabaseReference.push()
        val chatKey = chatRef.key
        chatRef.setValue(chatItem) { databaseError, _ ->
            if (databaseError != null) {
                Log.e(GroupRepositoryImpl.TAG, "Fail: ${databaseError.message}")
            } else {
                Log.e(GroupRepositoryImpl.TAG, "Success")
            }
        }
        return chatKey.toString()
    }

    override suspend fun loadMessageItems(chatId: String): Flow<MessageItemsEntity?> =
        callbackFlow {
            var flag = true

            val sortedDatabaseRef = chatDatabaseReference.child("message").orderByChild("timestamp")

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

                        if (flag) {
                            lastTimestamps[chatId] =
                                messageResponses.firstOrNull()?.timestamp ?: return
                        }

                        val entity = MessageItemsResponse(messageResponses).toEntity()
                        trySend(entity)

                        flag = false
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
                chatDatabaseReference.removeEventListener(listener)
            }
        }


    override suspend fun addChatMessageItem(userId: String, message: String, nickname: String) {
        timeDatabaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val offset = snapshot.value as Long
                val estimatedServerTimeMs = System.currentTimeMillis() + offset

                val messageRef = chatDatabaseReference.child("message").push()

                val messageData = Message(
                    content = message,
                    timestamp = estimatedServerTimeMs,
                    senderId = userId,
                    nickname = nickname,
                )

                // message 추가
                messageRef.setValue(messageData)

                // lastMessage 업데이트
                chatDatabaseReference.child("last").setValue(messageData)

                // FCM Notification 객체 생성
                val notification = GCMRequest(
                    to = WooDongApp.getApp().getString(R.string.test_client_token),
                    data = mapOf("action" to "ChatDetail"),
                    notification = mapOf(
                        "title" to "GCM Test : title",
                        "body" to "GCM Test : body"
                    ),
                )

                // TODO 나중에 메소드로 분리
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response =
                            GCMRetrofitClient.gcmRemoteSource.sendNotification(notification)

                        if (response.isSuccessful) {
                            Log.d(
                                TAG,
                                "Notification sent successfully. ${response.body()?.string()}"
                            )
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

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to get server time.", error.toException())
            }
        })
    }


    override fun initChatItemTimestamp(chatId: String, userId: String) {
        lastTimestamps.remove(chatId)
        timeDatabaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val offset = dataSnapshot.value as Long
                val estimatedServerTimeMs = System.currentTimeMillis() + offset

                lastSeemTime[chatId] = estimatedServerTimeMs

                // lastSeemTime 업데이트
                chatDatabaseReference.child("lastSeemTime").child(userId)
                    .setValue(estimatedServerTimeMs)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리
            }
        })
    }

    override suspend fun getChatId(groupId: String): Flow<String> = callbackFlow {
        chatDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { snapshot ->
                    if (snapshot.child("groupId").getValue(String::class.java) == groupId) {
                        trySend(snapshot.key.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })
    }
}