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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        runCatching {
            chatItem(user.chatIds.orEmpty()).collect { items ->
                val chatItemList = items?.chatItems?.map {
                    ChatItem.GroupChatItem(
                        id = it.id,
                        title = it.id,
                        imgProfile = it.imgProfile,
                        lastMessage = it.lastMessage,
                        location = it.location,
                        timeStamp = formatTimestamp(it.timestamp ?: System.currentTimeMillis()),
                    )
                }.orEmpty()
                _chatList.postValue(chatItemList.toMutableList())
            }
        }.onFailure {
            Log.e("danny", it.message.toString())
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val currentTimeMillis = System.currentTimeMillis()

        val currentTime = Date(currentTimeMillis)
        val messageTime = Date(timestamp)

        val diff = currentTime.time - messageTime.time
        val minute = 60 * 1000
        val hour = minute * 60
        val day = hour * 24
        val year = day * 365

        return when {
            diff < minute -> "방금 전"
            diff < 2 * minute -> "1분 전"
            diff < hour -> "${diff / minute}분 전"
            diff < 2 * hour -> "1시간 전"
            diff < day -> "${diff / hour}시간 전"
            diff < 2 * day -> "어제"
            diff < year -> SimpleDateFormat("MM월 dd일", Locale.KOREA).format(messageTime)
            else -> SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(messageTime)
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