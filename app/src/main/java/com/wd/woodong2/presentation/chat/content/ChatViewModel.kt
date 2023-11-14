package com.wd.woodong2.presentation.chat.content

import android.content.Context
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
import com.wd.woodong2.R
import com.wd.woodong2.data.repository.ChatRepositoryImpl
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.data.sharedpreference.SignInPreferenceImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.model.ChatItemsEntity
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.ChatGetItemsUseCase
import com.wd.woodong2.domain.usecase.UserGetItemUseCase
import com.wd.woodong2.domain.usecase.UserPrefGetItemUseCase
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getChatItemUseCase: ChatGetItemsUseCase,
    private val prefGetUserItemUseCase: UserPrefGetItemUseCase,
    private val getUserItemUseCase: UserGetItemUseCase,
) : ViewModel(
) {
    companion object {
        const val TAG = "ChatViewModel"
    }

    private val _chatList = MutableLiveData<MutableList<ChatItem>>()
    val chatList: LiveData<MutableList<ChatItem>>
        get() = _chatList

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isEmptyList: MutableLiveData<Boolean> = MutableLiveData()
    val isEmptyList: LiveData<Boolean> get() = _isEmptyList

    // User test
    var user: UserItem

    init {
        val getUser = prefGetUserItemUseCase()
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
            initChatItems()
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

    private fun initChatItems() = viewModelScope.launch {
        _isLoading.value = true
        runCatching {
            getUserItemUseCase(user.id ?: "").collect { item ->
                user = UserItem(
                    id = item?.id ?: "(알수 없음)",
                    name = item?.name ?: "(알수 없음)",
                    imgProfile = item?.imgProfile ?: "(알수 없음)",
                    email = item?.email ?: "(알수 없음)",
                    chatIds = item?.chatIds ?: emptyList(),
                    firstLocation = item?.firstLocation ?: "(알수 없음)",
                    secondLocation = item?.secondLocation ?: "(알수 없음)",
                    groupIds = item?.groupIds ?: emptyList(),        //모임
                    likedIds = item?.likedIds ?: emptyList(),        //좋아요 게시물
                    writtenIds = item?.writtenIds ?: emptyList(),        //작성한 게시물
                )

                getChatItemUseCase(user.chatIds.orEmpty()).collect { items ->
                    val chatItems = readChatItems(items)
                    _isEmptyList.value = chatItems.isEmpty()
                    _chatList.postValue(chatItems.toMutableList())
                    _isLoading.value = false
                }
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
            _isLoading.value = false
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
    }?.sortedByDescending { it.timeStamp }.orEmpty()

    fun reloadChatItems() = viewModelScope.launch {
        _isLoading.value = true
        _chatList.value = mutableListOf()
        runCatching {
            getChatItemUseCase(user.chatIds.orEmpty()).collect { items ->
                _chatList.postValue(readChatItems(items).toMutableList())
                _isLoading.value = false
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
            _isLoading.value = false
        }
    }
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

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(
                ChatGetItemsUseCase(chatRepositoryImpl),
                UserPrefGetItemUseCase(userPreferencesRepository),
                UserGetItemUseCase(userRepositoryImpl),
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}