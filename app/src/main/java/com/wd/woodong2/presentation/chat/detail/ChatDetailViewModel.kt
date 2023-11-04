package com.wd.woodong2.presentation.chat.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.wd.woodong2.data.repository.ChatRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.ChatGetMessageItemsUseCase
import com.wd.woodong2.domain.usecase.ChatSendMessageUseCase
import com.wd.woodong2.domain.usecase.SignInGetUserUIDUseCase
import com.wd.woodong2.domain.usecase.UserGetItemUseCase
import com.wd.woodong2.presentation.chat.content.ChatItem
import kotlinx.coroutines.launch

class ChatDetailViewModel(
    private val getMessageItem: ChatGetMessageItemsUseCase,
    private val sendMessageItem: ChatSendMessageUseCase,
    private val getUserUID: SignInGetUserUIDUseCase,
    private val getUser: UserGetItemUseCase,
) : ViewModel(
) {
    companion object {
        const val TAG = "ChatDetailViewModel"
    }

    private val _massageList = MutableLiveData<MutableList<MessageItem>>()
    val messageList: LiveData<MutableList<MessageItem>>
        get() = _massageList

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private lateinit var uid: String
    private lateinit var name: String

    init {
        getMessageItem()
        uid = getUserUID() ?: ""
        getUserEntity()
    }

    private fun getUserEntity() = viewModelScope.launch {
        runCatching {
            _isLoading.value = true
            getUser(uid).collect { user ->
                name = user?.name ?: "(알 수 없음)"
                _isLoading.value = false
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
            _isLoading.value = false
        }
    }

    private fun getMessageItem() = viewModelScope.launch {
        runCatching {
            _isLoading.value = true
            getMessageItem.invoke().collect { items ->
                val messageItemList = items?.messageItems?.map { messageResponse ->
                    MessageItem(
                        id = messageResponse.id,
                        content = messageResponse.content,
                        senderId = messageResponse.senderId,
                        timestamp = messageResponse.timestamp,
                        isMyMessage = messageResponse.senderId == uid,
                        nickname = messageResponse.nickname
                    )
                }?.sortedBy { it.timestamp } ?: emptyList()
                _massageList.postValue(messageItemList.toMutableList())
                _isLoading.value = false
            }
        }.onFailure {
            Log.e("danny", it.message.toString())
            _isLoading.value = false
        }
    }

    fun sendMessage(message: String) = viewModelScope.launch {
        runCatching {
            // Test
            sendMessageItem(uid, message, name)
        }.onFailure {
            Log.e("danny", it.message.toString())
        }
    }
}

class ChatDetailViewModelFactory(
    private val chatItem: ChatItem,
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

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatDetailViewModel::class.java)) {
            return ChatDetailViewModel(
                ChatGetMessageItemsUseCase(chatRepository),
                ChatSendMessageUseCase(chatRepository),
                SignInGetUserUIDUseCase(userRepositoryImpl),
                UserGetItemUseCase(userRepositoryImpl),
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}