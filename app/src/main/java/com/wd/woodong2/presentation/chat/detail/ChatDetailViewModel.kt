package com.wd.woodong2.presentation.chat.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.data.repository.ChatRepositoryImpl
import com.wd.woodong2.domain.usecase.ChatGetMessageItemsUseCase
import com.wd.woodong2.domain.usecase.ChatSendMessageUseCase
import com.wd.woodong2.presentation.chat.content.ChatItem
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val getMessageItem: ChatGetMessageItemsUseCase,
    private val sendMessageItem: ChatSendMessageUseCase,
    private val userId: String
) : ViewModel(
) {
    private val _massageList = MutableLiveData<MutableList<MessageItem>>()
    val messageList: LiveData<MutableList<MessageItem>>
        get() = _massageList

    init {
        getMessageItem()
    }

    private fun getMessageItem() = viewModelScope.launch {
        runCatching {
//            리스너 없는 코드
            getMessageItem.invoke().collect { items ->
                val messageItemList = items?.messageItems?.map { messageResponse ->
                    MessageItem(
                        id = messageResponse.id,
                        content = messageResponse.content,
                        senderId = messageResponse.senderId,
                        timestamp = messageResponse.timestamp,
                        isMyMessage = messageResponse.senderId == userId
                    )
                }?.sortedBy { it.timestamp } ?: emptyList()
                _massageList.postValue(messageItemList.toMutableList())
            }
        }.onFailure {
            Log.e("danny", it.message.toString())
        }
    }

    fun sendMessage(message: String) = viewModelScope.launch {
        runCatching {
            // Test
            sendMessageItem(userId, message)
        }.onFailure {
            Log.e("danny", it.message.toString())
        }
    }
}

class ChatDetailViewModelFactory(
    private val chatItem: ChatItem,
    private val userId: String,
) : ViewModelProvider.Factory {

    private val chatRepository by lazy {
        ChatRepositoryImpl(
            when (chatItem) {
                is ChatItem.GroupChatItem -> {
                    FirebaseDatabase.getInstance().getReference("chat_list").child("group")
                        .child(chatItem.id.orEmpty())
                }

                is ChatItem.PrivateChatItem -> {
                    FirebaseDatabase.getInstance().getReference("chat_list").child("private")
                        .child(chatItem.id.orEmpty())
                }
            }
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatDetailViewModel::class.java)) {
            return ChatDetailViewModel(
                ChatGetMessageItemsUseCase(chatRepository),
                ChatSendMessageUseCase(chatRepository),
                userId
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}