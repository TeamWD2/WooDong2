package com.wd.woodong2.presentation.chat.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.data.repository.MessageRepositoryImpl
import com.wd.woodong2.domain.usecase.MessageGetItemsUseCase
import com.wd.woodong2.domain.usecase.MessageSendItemsUseCase
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val messageGetItem: MessageGetItemsUseCase,
    private val messageSendItem: MessageSendItemsUseCase,
    private val chatKey: String,
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
            messageGetItem(chatKey).collect { items ->
                val messageItemList = items?.messageItems?.map { messageResponse ->
                    MessageItem(
                        id = messageResponse.id,
                        message = messageResponse.message,
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
            messageSendItem(chatKey, userId, message)
            _massageList.postValue(_massageList.value.orEmpty().toMutableList())
        }.onFailure {
            Log.e("danny", it.message.toString())
        }
    }
}

class ChatDetailViewModelFactory(
    private val chatKey: String,
    private val userId: String
) : ViewModelProvider.Factory {

    private val chatDatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("chats")
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatDetailViewModel::class.java)) {
            return ChatDetailViewModel(
                MessageGetItemsUseCase(MessageRepositoryImpl(chatDatabaseReference)),
                MessageSendItemsUseCase(MessageRepositoryImpl(chatDatabaseReference)),
                chatKey,
                userId
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}