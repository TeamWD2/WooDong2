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
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.model.GroupAlbumEntity
import com.wd.woodong2.domain.model.GroupBoardEntity
import com.wd.woodong2.domain.model.GroupIntroduceEntity
import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.model.GroupMainEntity
import com.wd.woodong2.domain.model.GroupMemberEntity
import com.wd.woodong2.domain.usecase.GroupGetItemsUseCase
import com.wd.woodong2.domain.usecase.UserPrefGetItemUseCase
import com.wd.woodong2.presentation.group.GroupUserInfoItem
import com.wd.woodong2.presentation.group.content.GroupItem
import kotlinx.coroutines.launch

class GroupDetailSharedViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val getGroupItems: GroupGetItemsUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "GroupDetailSharedViewModel"
    }

    var isJoinGroup: Boolean = false

    private val _tabName: MutableLiveData<Int> = MutableLiveData()
    val tabName: LiveData<Int> get() = _tabName

    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _groupDetailItem: MutableLiveData<List<GroupItem>> = MutableLiveData()
    val groupDetailItem: LiveData<List<GroupItem>> get() = _groupDetailItem

    fun initIsJoinGroup(groupDetailContentType: GroupDetailContentType?) {
        isJoinGroup = groupDetailContentType == GroupDetailContentType.WRITE_BOARD
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
        itemId: String
    ): List<GroupItem> = readGroupItems(items).filter {
        it.id == itemId
    }

    /**
     * Firebase 에서 모임 목록 read
     */
    private fun readGroupItems(
        items: GroupItemsEntity
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
                    backgroundImage = entity.backgroundImage
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
}

class GroupDetailSharedViewModelFactory(
    val context: Context
) : ViewModelProvider.Factory {
    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val userPrefRepository = UserPreferencesRepositoryImpl(
            null,
            UserInfoPreferenceImpl(
                context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
            )
        )
        val databaseReference = FirebaseDatabase.getInstance().getReference("group_list")
        val groupGetRepository = GroupRepositoryImpl(databaseReference)
        if (modelClass.isAssignableFrom(GroupDetailSharedViewModel::class.java)) {
            return GroupDetailSharedViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                GroupGetItemsUseCase(groupGetRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}