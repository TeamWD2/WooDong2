package com.wd.woodong2.presentation.chat.content

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wd.woodong2.data.repository.ChatRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.usecase.ChatGetItemsUseCase
import com.wd.woodong2.domain.usecase.UserGetItemsUseCase
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatItem: ChatGetItemsUseCase,
    private val userItem: UserGetItemsUseCase
) : ViewModel(
) {
    private val _chatList = MutableLiveData<MutableList<ChatItem>>()
    val chatList: LiveData<MutableList<ChatItem>>
        get() = _chatList

    // test
    val userId = "user2"
    lateinit var user: UserItem

    init {
        getUserItem()
    }

    private fun getChatItem() = viewModelScope.launch {
        runCatching {
            chatItem(user.chatIds.orEmpty()) { items ->
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

    private fun getUserItem() = viewModelScope.launch {
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

                runCatching {
                    chatItem(user.chatIds.orEmpty()) { items ->
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
        }.onFailure {
            Log.e("sinw", it.message.toString())
        }

    }
}


class ChatViewModelFactory(
    private val chatRepository: ChatRepositoryImpl,
    private val userRepository: UserRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(
                ChatGetItemsUseCase(chatRepository),
                UserGetItemsUseCase(userRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}