package com.wd.woodong2.presentation.chat.content

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.data.repository.ChatRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.usecase.ChatGetItemsUseCase
import com.wd.woodong2.domain.usecase.UserGetItemsUseCase
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatItem: ChatGetItemsUseCase,
    private val userItem: UserGetItemsUseCase,
) : ViewModel(
) {
    private val _chatList = MutableLiveData<MutableList<ChatItem>>()
    val chatList: LiveData<MutableList<ChatItem>>
        get() = _chatList

    // User test
    val userId = "user1"
    var user = UserItem(
        id = "user1",
        chatIds = listOf("chat1", "chat2"),
        email = "대니주@example.com",
        name = "주찬영",
        imgProfile = "URL_TO_USER_1_IMAGE"
    )

    init {
        getChatItem()
    }

    private fun getUserItem() = viewModelScope.launch {
        // userId로 채팅방 찾기
        runCatching {
            userItem(userId) { items ->
                val userItem = items?.userItems?.map {
                    UserItem(
                        id = it.id,
                        name = it.name,
                        imgProfile = it.imgProfile,
                        email = it.email,
                        chatIds = it.chatIds,
                    )
                }.orEmpty()
                user = userItem[0]
            }
        }.onFailure {
            Log.e("sinw", it.message.toString())
        }
    }

    private fun getChatItem() = viewModelScope.launch {
        // userId로 채팅방 찾기
//        runCatching {
//            runBlocking {
//                userItem(userId) { items ->
//                    val userItem = items?.userItems?.map {
//                        UserItem(
//                            id = it.id,
//                            name = it.name,
//                            imgProfile = it.imgProfile,
//                            email = it.email,
//                            chatIds = it.chatIds,
//                        )
//                    }.orEmpty()
//                    user = userItem[0]
//                    Log.d("danny", "User 받아오기 성공 : $user")
//                }
//            }
//        }.onFailure {
//            Log.e("danny", it.message.toString())
//        }

        runCatching {
            chatItem(user.chatIds.orEmpty()).collect { items ->
                val chatItemList = items?.chatItems?.map {
                    ChatItem.GroupChatItem(
                        id = it.id,
                        title = it.id,
                        imgProfile = it.imgProfile,
                        lastMessage = it.lastMessage,
                        location = it.location,
                        timeStamp = it.timestamp,
                    )
                }.orEmpty()
                _chatList.postValue(chatItemList.toMutableList())
            }
        }.onFailure {
            Log.e("danny", it.message.toString())
        }
    }
}


class ChatViewModelFactory() : ViewModelProvider.Factory {

    private val chatDatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("chats")
    }
    private val userDatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("users")
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(
                ChatGetItemsUseCase(ChatRepositoryImpl(chatDatabaseReference)),
                UserGetItemsUseCase(UserRepositoryImpl(userDatabaseReference))
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}