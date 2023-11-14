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
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.model.GroupMainEntity
import com.wd.woodong2.domain.model.GroupMemberEntity
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.ChatInitChatItemTimestampUseCase
import com.wd.woodong2.domain.usecase.ChatLoadMessageItemsUseCase
import com.wd.woodong2.domain.usecase.ChatSendMessageUseCase
import com.wd.woodong2.domain.usecase.GroupGetItemUseCase
import com.wd.woodong2.domain.usecase.SignInGetUserUIDUseCase
import com.wd.woodong2.domain.usecase.UserGetItemUseCase
import com.wd.woodong2.presentation.chat.content.ChatItem
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.group.content.GroupItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChatDetailViewModel(
    private val chatItem: ChatItem,
    private val loadMessageItem: ChatLoadMessageItemsUseCase,
    private val sendMessageItem: ChatSendMessageUseCase,
    private val getUserUID: SignInGetUserUIDUseCase,
    private val getUser: UserGetItemUseCase,
    private val initChatItemTimestamp: ChatInitChatItemTimestampUseCase,
    private val getGroupMainItemUseCase: GroupGetItemUseCase,
) : ViewModel(
) {
    companion object {
        const val TAG = "ChatDetailViewModel"
    }

    private val _massageList = MutableLiveData<MutableList<MessageItem>>()
    val messageList: LiveData<MutableList<MessageItem>>
        get() = _massageList

    private val _curGroupInfo = MutableLiveData<ChatDetailItem>()
    val curGroupInfo: LiveData<ChatDetailItem>
        get() = _curGroupInfo

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private lateinit var uid: String
    private var curUser: UserItem? = null
    private var lastMessage: MessageItem? = null

    init {
        getMessageItem()
        uid = getUserUID() ?: ""
        getUserEntity()
        getGroupInfo()
    }

    private fun getUserEntity() = viewModelScope.launch {
        runCatching {
            _isLoading.value = true
            getUser(uid).collect { user ->
                if (user != null) {
                    curUser = UserItem(
                        id = user.id,
                        name = user.name,
                        imgProfile = user.imgProfile,
                        email = user.email,
                        chatIds = user.chatIds,
                        groupIds = user.groupIds,
                        likedIds = user.likedIds,
                        writtenIds = user.writtenIds,
                        firstLocation = user.firstLocation,
                        secondLocation = user.secondLocation,
                    )
                }
                _isLoading.value = false
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
            _isLoading.value = false
        }
    }

    private fun getGroupInfo() = viewModelScope.launch {
        runCatching {
            _isLoading.value = true

            if (chatItem is ChatItem.GroupChatItem) {
                getGroupMainItemUseCase(chatItem.groupId ?: "").collect { group ->

                    val curGroup = group?.groupList
                    var chatDetailItem = ChatDetailItem()

                    if (curGroup != null) {
                        curGroup.forEach { groupEntity ->
                            when (groupEntity) {
                                is GroupMainEntity -> chatDetailItem = chatDetailItem.copy(
                                    id = groupEntity.id,
                                    groupName = groupEntity.groupName,
                                    memberLimit = groupEntity.memberLimit,
                                    mainImage = groupEntity.mainImage
                                )

                                is GroupMemberEntity -> chatDetailItem = chatDetailItem.copy(
                                    title = groupEntity.title,
                                    memberList = groupEntity.memberList?.map { member ->
                                        GroupItem.Member(
                                            userId = member.userId,
                                            profile = member.profile,
                                            name = member.name,
                                            location = member.location,
                                            comment = member.comment
                                        )
                                    },
                                )

                                else -> {}
                            }
                        }

                        _curGroupInfo.value = chatDetailItem
                    }
                    _isLoading.value = false
                }
            }

        }.onFailure {
            Log.e(TAG, it.message.toString())
            _isLoading.value = false
        }
    }

    fun getMessageItem() = viewModelScope.launch {
        runCatching {
            _isLoading.value = true
            if (chatItem.id == null) return@launch
            loadMessageItem(chatItem.id!!).collect { items ->
                val messageItems = items?.messageItems ?: emptyList()

                val messageItemList = mutableListOf<MessageItem>()

                for (i in messageItems.indices) {
                    val messageResponse = messageItems[i]
                    val previousTimestamp = messageItems.getOrNull(i - 1)?.timestamp

                    val dateToShow = if (previousTimestamp != null && isOverADay(
                            previousTimestamp,
                            messageResponse.timestamp ?: 0
                        )
                    ) {
                        formatDate(messageResponse.timestamp)
                    } else {
                        null
                    }
                    messageItemList.add(
                        MessageItem(
                            id = messageResponse.id,
                            content = messageResponse.content,
                            senderId = messageResponse.senderId,
                            timestamp = messageResponse.timestamp,
                            isMyMessage = messageResponse.senderId == uid,
                            nickname = messageResponse.nickname,
                            profileImg = messageResponse.profileImg,
                            dateToShow = dateToShow
                        )
                    )
                }

                val updatedMessageList = _massageList.value.orEmpty().toMutableList()

                messageItemList.forEach { messageItem ->
                    if (updatedMessageList.none { it.id == messageItem.id }) {
                        updatedMessageList.add(messageItem)
                    }
                }

                val list = updatedMessageList.sortedBy { it.timestamp }.toMutableList()

                _massageList.postValue(list)
                _isLoading.value = false
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
            _isLoading.value = false
        }
    }


    fun sendMessage(message: String) = viewModelScope.launch {
        runCatching {
            // Test
            sendMessageItem(uid, message, curUser?.name ?: "(알 수 없음)", curUser?.imgProfile ?: "")
        }.onFailure {
            Log.e(TAG, it.message.toString())
        }
    }

    fun destroyAll() {
        val chatId = chatItem.id
        if (chatId != null) {
            initChatItemTimestamp(chatId, uid)
        }
    }

    fun setLastMessage() {
        val lastMessage = _massageList.value?.last()
        this.lastMessage = lastMessage
    }

    fun isNewMessage(): Boolean {
        val savedLastMessage = lastMessage
        val loadLastMessage = _massageList.value?.last()

        if (savedLastMessage == null || loadLastMessage == null) {
            return false
        }

        return savedLastMessage.id != loadLastMessage.id
    }

    private fun isOverADay(previousTimestamp: Long, currentTimestamp: Long): Boolean {
        val previousCalendar = Calendar.getInstance().apply { timeInMillis = previousTimestamp }
        val currentCalendar = Calendar.getInstance().apply { timeInMillis = currentTimestamp }

        return previousCalendar.get(Calendar.DAY_OF_YEAR) != currentCalendar.get(Calendar.DAY_OF_YEAR) ||
                previousCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR)
    }

    private fun formatDate(timestamp: Long?): String? {
        return if (timestamp != null) {
            val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        } else {
            null
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
            },
            FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset")
        )
    }

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }

    private val groupRepositoryImpl by lazy {
        GroupRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("group_list"),
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatDetailViewModel::class.java)) {
            return ChatDetailViewModel(
                chatItem,
                ChatLoadMessageItemsUseCase(chatRepository),
                ChatSendMessageUseCase(chatRepository),
                SignInGetUserUIDUseCase(userRepositoryImpl),
                UserGetItemUseCase(userRepositoryImpl),
                ChatInitChatItemTimestampUseCase(chatRepository),
                GroupGetItemUseCase(groupRepositoryImpl),
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}