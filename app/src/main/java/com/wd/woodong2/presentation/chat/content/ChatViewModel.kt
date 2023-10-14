package com.wd.woodong2.presentation.chat.content

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wd.woodong2.data.repository.ChatRepositoryImpl
import com.wd.woodong2.domain.usecase.ChatGetItemsUseCase
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatItem: ChatGetItemsUseCase
) : ViewModel(
) {
    private val _chatList = MutableLiveData<MutableList<ChatItem>>()
    val chatList: LiveData<MutableList<ChatItem>>
        get() = _chatList

    init {
        getChatItem()
    }

    fun getChatItem() = viewModelScope.launch {
        runCatching {
            chatItem { items ->
                val chatItemList = items?.chatItems?.map {
                    ChatItem.GroupChatItem(
                        title = it.id,
                        imgProfile = it.imgProfile,
                        lastMassage = it.lastMassage,
                        location = it.location,
                        timeStamp = it.timestamp,
                    )
                }.orEmpty()
                _chatList.postValue(chatItemList.toMutableList())
            }
        }.onFailure {
            Log.e("sinw", it.message.toString())
        }
    }
}


class ChatViewModelFactory(
    private val repository: ChatRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(
                ChatGetItemsUseCase(repository)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}