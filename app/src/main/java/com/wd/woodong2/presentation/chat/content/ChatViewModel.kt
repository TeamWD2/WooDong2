package com.wd.woodong2.presentation.chat.content

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.R
import com.wd.woodong2.data.repository.ChatRepositoryImpl
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.sharedpreference.SignInPreferenceImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.model.ChatItemsEntity
import com.wd.woodong2.domain.usecase.ChatGetItemsUseCase
import com.wd.woodong2.domain.usecase.UserPrefGetItemUseCase
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatItem: ChatGetItemsUseCase,
    private val prefGetUserItem: UserPrefGetItemUseCase,
) : ViewModel(
) {
    private val _chatList = MutableLiveData<MutableList<ChatItem>>()
    val chatList: LiveData<MutableList<ChatItem>>
        get() = _chatList

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // User test
    var user: UserItem

    init {
        val getUser = prefGetUserItem()
        if (getUser != null) {
            user = UserItem(
                id = getUser.id,
                name = getUser.name,
                imgProfile = getUser.imgProfile,
                email = getUser.email,
                chatIds = getUser.chatIds,
                firstLocation = getUser.firstLocation,
                secondLocation = getUser.secondLocation,
                groupIds = emptyList(),        //모임
                likedIds = emptyList(),        //좋아요 게시물
                writtenIds = emptyList(),        //작성한 게시물
            )
            getChatItems()
        } else {
            user = UserItem(
                id = "(알수 없음)",
                name = "(알수 없음)",
                imgProfile = "(알수 없음)",
                email = "(알수 없음)",
                chatIds = emptyList(),
                firstLocation = "(알수 없음)",
                secondLocation = "(알수 없음)",
                groupIds = emptyList(),        //모임
                likedIds = emptyList(),        //좋아요 게시물
                writtenIds = emptyList(),        //작성한 게시물
            )
        }

    }

    private fun getChatItems() = viewModelScope.launch {
        _isLoading.value = true
        runCatching {
            chatItem(user.chatIds.orEmpty()).collect { items ->
                _chatList.postValue(readChatItems(items).toMutableList())
                _isLoading.value = false
            }
        }.onFailure {
            Log.e("danny", it.message.toString())
            _isLoading.value = false
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
        items: ChatItemsEntity?,
    ) = items?.chatItems?.map {
        ChatItem.GroupChatItem(
            id = it.id,
            groupId = it.groupId,
            lastMessage = it.last?.content ?: "",
            timeStamp = it.last?.timestamp,
            mainImage = it.mainImage,
            memberLimit = it.memberLimit,
            title = it.title,
            isRead = (it.lastSeemTime?.get(user.id ?: "") ?: 0) > (it.last?.timestamp ?: 0)
        )
    }.orEmpty()
}

class ChatViewModelFactory(
    val context: Context,
) : ViewModelProvider.Factory {
    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)

    private val chatRepositoryImpl by lazy {
        ChatRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("chat_list"),
            FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset")
        )
    }

    private val userPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(
            SignInPreferenceImpl(
                context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
            ),
            UserInfoPreferenceImpl(
                context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
            )
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(
                ChatGetItemsUseCase(chatRepositoryImpl),
                UserPrefGetItemUseCase(userPreferencesRepository),
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}