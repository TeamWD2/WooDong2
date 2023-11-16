package com.wd.woodong2.presentation.mypage.content.group

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
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.data.sharedpreference.SignInPreferenceImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.model.GroupAlbumEntity
import com.wd.woodong2.domain.model.GroupBoardEntity
import com.wd.woodong2.domain.model.GroupIntroduceEntity
import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.model.GroupMainEntity
import com.wd.woodong2.domain.model.GroupMemberEntity
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.group.GroupGetItemsUseCase
import com.wd.woodong2.domain.usecase.user.UserGetItemUseCase
import com.wd.woodong2.domain.usecase.prefs.UserPrefGetItemUseCase
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.group.content.GroupItem
import kotlinx.coroutines.launch

class MyPageGroupViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val groupGetItems: GroupGetItemsUseCase,
    private val userItem: UserGetItemUseCase,
) : ViewModel(){
    companion object {
        private const val TAG = "GroupViewModel"
    }
    private val _groupList: MutableLiveData<List<GroupItem>> = MutableLiveData()
    val groupList: LiveData<List<GroupItem>> get() = _groupList

    private val _printList: MutableLiveData<List<GroupItem>> = MutableLiveData()
    val printList: LiveData<List<GroupItem>> get() = _printList

    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _isEmptyList: MutableLiveData<Boolean> = MutableLiveData()
    val isEmptyList: LiveData<Boolean> get() = _isEmptyList

    val userId= getUserInfo()?.id ?: "UserId"
    private var userInfo: MutableLiveData<UserItem> = MutableLiveData()

    init {
        getUserItem()
    }

    fun printListSet()= viewModelScope.launch{
        _loadingState.value = true
        runCatching {
            _printList.value = groupList.value?.filter { item ->
                userInfo.value?.groupIds!!.contains(item.id)
            }
            _isEmptyList.value = _printList.value?.isEmpty()
            _loadingState.value = false
        }.onFailure {
            _loadingState.value = false
        }
    }

    fun getGroupItem() = viewModelScope.launch {
        _loadingState.value = true
        runCatching {
            groupGetItems().collect { items ->
                val groupItems = readGroupItems(items)
                _isEmptyList.value = groupItems.isEmpty()
                _groupList.postValue(groupItems)
                _loadingState.value = false
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
            _loadingState.value = false
        }
    }

    /**
     * 로그인 된 계정의 선택한 모임 가입 여부 확인
     */
    fun isUserInGroup(groupId: String?, userId: String?): Boolean {
        /**
         * ViewType(Main)의 id와 동일한 id를 가진 항목 찾기
         */
        fun getRelatedItems(id: String?): List<GroupItem> {
            if (id == null) {
                return emptyList()
            }
            return groupList.value?.filter {
                it.id == id
            } ?: emptyList()
        }

        if(userId == null) {
            return false
        }
        return getRelatedItems(groupId).any { groupItem ->
            when(groupItem) {
                is GroupItem.GroupMember ->
                    groupItem.memberList?.any {
                        it.userId == userId
                    } == true
                else -> false
            }
        }
    }

    /**
     * Firebase 에서 모임 목록 read
     */
    private fun readGroupItems(
        items: GroupItemsEntity
    ): List<GroupItem> {
        return items.groupList.map { entity ->
            when(entity) {
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
                                commentList = board.commentList?.toSortedMap()?.mapValues { (commentId, comment) ->
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
        }
    }
    private fun getUserItem() = viewModelScope.launch {
        runCatching {
            userItem(userId).collect { user ->
                val userItem =
                    UserItem(
                        id = user?.id ?: "",
                        name = user?.name ?: "",
                        imgProfile = user?.imgProfile ?: "",
                        email = user?.email ?: "",
                        chatIds = user?.chatIds.orEmpty(),
                        groupIds = user?.groupIds.orEmpty(),        //모임
                        likedIds = user?.likedIds.orEmpty(),        //좋아요 게시물
                        writtenIds = user?.writtenIds.orEmpty(),        //작성한 게시물
                        firstLocation = user?.firstLocation ?: "",
                        secondLocation = user?.secondLocation ?: ""
                    )

                userInfo.postValue(userItem)
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
        }
    }

    fun getUserInfo() =
        prefGetUserItem()?.let {
            UserItem(
                id = it.id ?: "unknown",
                name = it.name ?: "unknown",
                imgProfile = it.imgProfile,
                email = it.email ?: "unknown",
                chatIds = it.chatIds,
                groupIds = it.groupIds,
                likedIds = it.likedIds,
                writtenIds = it.writtenIds,
                firstLocation = it.firstLocation ?: "unknown",
                secondLocation = it.secondLocation ?: "unknown"
            )
        }
}

class MyPageGroupViewModelFactory(
    val context: Context
) : ViewModelProvider.Factory {
    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)
    private val databaseReference = FirebaseDatabase.getInstance()

    val userPrefRepository = UserPreferencesRepositoryImpl(
        SignInPreferenceImpl(
            context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
        ),
        UserInfoPreferenceImpl(
            context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
        )
    )

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("user_list"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val databaseReference = FirebaseDatabase.getInstance().getReference("group_list")
        val repository = GroupRepositoryImpl(databaseReference)
        if (modelClass.isAssignableFrom(MyPageGroupViewModel::class.java)) {
            return MyPageGroupViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                GroupGetItemsUseCase(repository),
                UserGetItemUseCase(userRepositoryImpl),
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}