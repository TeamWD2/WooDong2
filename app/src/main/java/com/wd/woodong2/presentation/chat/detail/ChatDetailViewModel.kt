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
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val messageItem: MessageGetItemsUseCase,
    private val chatKey: String,
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
            messageItem(chatKey) { items ->
                val messageItemList = items?.messageItems?.map {
                    MessageItem(
                        id = it.id,
                        message = it.message,
                        senderId = it.senderId,
                        timestamp = it.timestamp,
                    )
                }.orEmpty()
                _massageList.postValue(messageItemList.toMutableList())
            }
        }.onFailure {
            Log.e("danny", it.message.toString())
        }
    }
}


class ChatDetailViewModelFactory(
    private val chatKey: String
) : ViewModelProvider.Factory {

    private val chatDatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("chats")
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatDetailViewModel::class.java)) {
            return ChatDetailViewModel(
                MessageGetItemsUseCase(MessageRepositoryImpl(chatDatabaseReference)),
                chatKey
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}