package com.wd.woodong2.presentation.group.detail

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
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.model.GroupAlbumEntity
import com.wd.woodong2.domain.model.GroupBoardEntity
import com.wd.woodong2.domain.model.GroupIntroduceEntity
import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.model.GroupMainEntity
import com.wd.woodong2.domain.model.GroupMemberEntity
import com.wd.woodong2.domain.usecase.ChatGetChatIdUseCase
import com.wd.woodong2.domain.usecase.GroupGetItemsUseCase
import com.wd.woodong2.domain.usecase.GroupGetMemberListUseCase
import com.wd.woodong2.domain.usecase.GroupSetMemberItemUseCase
import com.wd.woodong2.domain.usecase.UserPrefGetItemUseCase
import com.wd.woodong2.domain.usecase.UserSendPushMessageUseCase
import com.wd.woodong2.domain.usecase.UserUpdateGroupInfoUseCase
import com.wd.woodong2.presentation.group.GroupUserInfoItem
import com.wd.woodong2.presentation.group.content.GroupItem
import kotlinx.coroutines.launch

class GroupDetailSharedViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val getGroupItems: GroupGetItemsUseCase,
    private val getChatId: ChatGetChatIdUseCase,
    private val updateGroupInfo: UserUpdateGroupInfoUseCase,
    private val groupSetMemberItem: GroupSetMemberItemUseCase,
    private val groupGetMemberList: GroupGetMemberListUseCase,
    private val sendPushMessage: UserSendPushMessageUseCase,
) : ViewModel() {
    companion object {
        private const val TAG = "GroupDetailSharedViewModel"
    }

    private val _isJoinGroup: MutableLiveData<Boolean> = MutableLiveData()
    val isJoinGroup: LiveData<Boolean> get() = _isJoinGroup

    private val _tabName: MutableLiveData<Int> = MutableLiveData()
    val tabName: LiveData<Int> get() = _tabName

    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _groupDetailItem: MutableLiveData<List<GroupItem>> = MutableLiveData()
    val groupDetailItem: LiveData<List<GroupItem>> get() = _groupDetailItem

    private val _isSuccessJoinGroup: MutableLiveData<Boolean> = MutableLiveData()
    val isSuccessJoinGroup: LiveData<Boolean> get() = _isSuccessJoinGroup

    /**
     * 로그인 된 계정의 선택한 모임 가입 여부 확인
     */
    fun initIsJoinGroup() {
        _isJoinGroup.value = groupDetailItem.value?.filterIsInstance<GroupItem.GroupMember>()
            ?.firstOrNull()?.memberList?.any {
                it.userId == prefGetUserItem().let { userInfo ->
                    userInfo?.id
                }
            } == true
    }

    fun getUserInfo() =
        prefGetUserItem()?.let {
            GroupUserInfoItem(
                userId = it.id ?: "(알 수 없음)",
                userProfile = it.imgProfile,
                userName = it.name ?: "(알 수 없음)",
                userLocation = it.firstLocation ?: "(알 수 없음)"
            )
        }

    fun getGroupDetailItem(itemId: String?) {
        if (itemId == null) {
            return
        }

        viewModelScope.launch {
            _loadingState.value = true
            runCatching {
                getGroupItems().collect { items ->
                    _groupDetailItem.postValue(getRelatedItems(items, itemId))
                    _loadingState.value = false
                }
            }.onFailure {
                Log.e(TAG, it.message.toString())
                _loadingState.value = false
            }
        }
    }

    private fun getRelatedItems(
        items: GroupItemsEntity,
        itemId: String,
    ): List<GroupItem> = readGroupItems(items).filter {
        it.id == itemId
    }

    /**
     * Firebase 에서 모임 목록 read
     */
    private fun readGroupItems(
        items: GroupItemsEntity,
    ): List<GroupItem> {
        return items.groupList.map { entity ->
            when (entity) {
                is GroupMainEntity -> GroupItem.GroupMain(
                    id = entity.id,
                    title = "Main",
                    groupName = entity.groupName,
                    introduce = entity.introduce,
                    groupTag = entity.groupTag,
                    ageLimit = entity.ageLimit,
                    memberLimit = entity.memberLimit,
                    password = entity.password,
                    mainImage = entity.mainImage,
                    backgroundImage = entity.backgroundImage,
                    groupLocation = entity.groupLocation
                )

                is GroupIntroduceEntity -> GroupItem.GroupIntroduce(
                    id = entity.id,
                    title = entity.title,
                    introduce = entity.introduce,
                    groupTag = entity.groupTag,
                    ageLimit = entity.ageLimit,
                    memberLimit = entity.memberLimit,
                )

                is GroupMemberEntity -> GroupItem.GroupMember(
                    id = entity.id,
                    title = entity.title,
                    memberList = entity.memberList?.map { member ->
                        GroupItem.Member(
                            userId = member.userId,
                            profile = member.profile,
                            name = member.name,
                            location = member.location,
                            comment = member.comment
                        )
                    }
                )

                is GroupBoardEntity -> GroupItem.GroupBoard(
                    id = entity.id,
                    title = entity.title,
                    boardList = entity.boardList?.toSortedMap(reverseOrder())
                        ?.mapValues { (boardId, board) ->
                            GroupItem.Board(
                                boardId = boardId,
                                userId = board.userId,
                                profile = board.profile,
                                name = board.name,
                                location = board.location,
                                timestamp = board.timestamp,
                                content = board.content,
                                images = board.images,
                                commentList = board.commentList?.toSortedMap()
                                    ?.mapValues { (commentId, comment) ->
                                        GroupItem.BoardComment(
                                            commentId = commentId,
                                            userId = comment.userId,
                                            userProfile = comment.userProfile,
                                            userName = comment.userName,
                                            userLocation = comment.userLocation,
                                            timestamp = comment.timestamp,
                                            comment = comment.comment
                                        )
                                    }?.values?.toList()
                            )
                        }?.values?.toList()
                )

                is GroupAlbumEntity -> GroupItem.GroupAlbum(
                    id = entity.id,
                    title = entity.title,
                    images = entity.images?.toSortedMap(reverseOrder())?.values?.toList()
                )
            }
        }.sortedBy { it.id }
    }

    fun modifyTab(tabName: Int) {
        _tabName.value = tabName
    }

    /**
     * 모임 가입 여부 확인 (1. 가입 제한 멤버 수 확인)
     */
    fun isJoinPossibleMemberLimit(): Boolean {
        val groupMainItem =
            groupDetailItem.value?.filterIsInstance<GroupItem.GroupMain>()?.firstOrNull()
        return if (groupMainItem?.memberLimit.equals("제한 없음")) {
            true
        } else {
            val groupMemberItem =
                groupDetailItem.value?.filterIsInstance<GroupItem.GroupMember>()?.firstOrNull()
            ((groupMainItem?.memberLimit?.filter { it.isDigit() }?.toInt()
                ?: 0) > (groupMemberItem?.memberList?.size ?: 0))
        }
    }

    /**
     * 모임 가입 여부 확인 (2. 비밀번호 존재 유무 확인 및 비밀번호 일치 여부 확인)
     */
    fun isExistPassword(): Boolean {
        val groupMainItem =
            groupDetailItem.value?.filterIsInstance<GroupItem.GroupMain>()?.firstOrNull()
        return !(groupMainItem?.password.equals("[WD2] No Password"))
    }

    fun checkPassword(password: String): Boolean {
        val groupMainItem =
            groupDetailItem.value?.filterIsInstance<GroupItem.GroupMain>()?.firstOrNull()
        return (groupMainItem?.password.equals(password))
    }

    /**
     * 모임 가입 시, User 정보 업데이트 및 멤버 추가
     */
    fun updateUserInfo(groupId: String?) {
        if (groupId == null) {
            return
        }
        viewModelScope.launch {
            runCatching {
                val groupMainItem =
                    groupDetailItem.value?.filterIsInstance<GroupItem.GroupMain>()?.firstOrNull()
                if (groupMainItem != null) {
                    val userInfo = getUserInfo()
                    //User 정보 업데이트
                    var chatId: String? = null
                    getChatId(groupId).collect { id ->
                        chatId = id
                    }

                    // 새로 가입한 멤버 업데이트
                    updateGroupInfo(userInfo?.userId ?: "UserId", groupId, chatId)

                    //멤버 추가
                    groupSetMemberItem(
                        groupId,
                        GroupDetailMemberAddItem(
                            userId = userInfo?.userId ?: "UserId",
                            profile = userInfo?.userProfile,
                            name = userInfo?.userName ?: "UserName",
                            location = userInfo?.userLocation ?: "UserLocation",
                            comment = "모임 멤버"
                        )
                    )

                    // 가입 알람 전송
                    groupSendPushMessage(groupId)

                    _isSuccessJoinGroup.value = true
                } else {
                    Log.d(TAG, "groupMainItem is null")
                    _isSuccessJoinGroup.value = false
                }
            }.onFailure {
                Log.e(TAG, it.message.toString())
                _isSuccessJoinGroup.value = false
            }
        }
    }

    private suspend fun groupSendPushMessage(groupId: String) = viewModelScope.launch {
        runCatching {
            groupGetMemberList(groupId).collect { memberList ->
                sendPushMessage(memberList)
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
            _isSuccessJoinGroup.value = false
        }
    }
}

class GroupDetailSharedViewModelFactory(
    val context: Context,
) : ViewModelProvider.Factory {
    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)
    private val databaseReference = FirebaseDatabase.getInstance()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val userPrefRepository = UserPreferencesRepositoryImpl(
            null,
            UserInfoPreferenceImpl(
                context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
            )
        )
        val groupRepository =
            GroupRepositoryImpl(databaseReference.getReference("group_list"))
        val chatGetRepository =
            ChatRepositoryImpl(databaseReference.getReference("chat_list").child("group"), null)
        val userUpdateRepository =
            UserRepositoryImpl(databaseReference.getReference("users"), null, null)
        if (modelClass.isAssignableFrom(GroupDetailSharedViewModel::class.java)) {
            return GroupDetailSharedViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                GroupGetItemsUseCase(groupRepository),
                ChatGetChatIdUseCase(chatGetRepository),
                UserUpdateGroupInfoUseCase(userUpdateRepository),
                GroupSetMemberItemUseCase(groupRepository),
                GroupGetMemberListUseCase(groupRepository),
                UserSendPushMessageUseCase(userUpdateRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}