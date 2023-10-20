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
import com.wd.woodong2.domain.model.ChatItemsEntity
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
        chatIds = listOf(
            "-chat_list-group-TestData0", "-chat_list-group-TestData1"
        ),
        email = "대니주@example.com",
        name = "주찬영",
        imgProfile = "URL_TO_USER_1_IMAGE"
    )

    init {
        getChatItems()
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

    private fun getChatItems() = viewModelScope.launch {
        runCatching {
            chatItem(user.chatIds.orEmpty()).collect { items ->
                _chatList.postValue(readChatItems(items).toMutableList())
            }
        }.onFailure {
            Log.e("danny", it.message.toString())
        }
    }

    fun reloadChatItems() = viewModelScope.launch {
        _chatList.value?.clear()
        runCatching {
            chatItem(user.chatIds.orEmpty()).collect { items ->
                _chatList.postValue(readChatItems(items).toMutableList())
            }
        }.onFailure {
            Log.e("danny", it.message.toString())
        }
    }

    /**
     * Firebase 에서 chat 목록 read
     */
    private fun readChatItems(
        items: ChatItemsEntity?
    ) = items?.chatItems?.map {
        ChatItem.GroupChatItem(
            id = it.id,
            groupId = it.groupId,
            lastMessage = it.last?.content ?: "",
            timeStamp = it.last?.timestamp,
            mainImage = it.mainImage,
            memberLimit = it.memberLimit,
            title = it.title,
        )
    }.orEmpty()
}

class ChatViewModelFactory : ViewModelProvider.Factory {

    private val chatDatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("chat_list")
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