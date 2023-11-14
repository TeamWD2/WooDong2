package com.wd.woodong2.presentation.group.detail.board.detail

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
import com.wd.woodong2.domain.usecase.GroupAddBoardCommentUseCase
import com.wd.woodong2.domain.usecase.GroupDeleteBoardCommentUseCase
import com.wd.woodong2.domain.usecase.UserPrefGetItemUseCase
import com.wd.woodong2.presentation.group.GroupUserInfoItem
import com.wd.woodong2.presentation.group.content.GroupItem
import kotlinx.coroutines.launch

class GroupDetailBoardDetailViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val groupAddBoardCommentItem: GroupAddBoardCommentUseCase,
    private val groupDeleteBoardCommentItem: GroupDeleteBoardCommentUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "GroupDetailBoardDetailViewModel"
    }

    private val _groupBoardItem: MutableLiveData<List<GroupDetailBoardDetailItem>> =
        MutableLiveData()
    val groupBoardItem: LiveData<List<GroupDetailBoardDetailItem>> get() = _groupBoardItem

    private val _isSuccessAddComment: MutableLiveData<Boolean> = MutableLiveData()
    val isSuccessAddComment: LiveData<Boolean> get() = _isSuccessAddComment

    /**
     * 넘겨받아온 데이터 화면에 출력하기 위해 ViewType 별로 가공
     */
    fun initGroupBoardItem(groupBoardItem: GroupItem.Board?) {
        val boardItem = mutableListOf<GroupDetailBoardDetailItem>()
        groupBoardItem?.let { board ->
            boardItem.add(
                GroupDetailBoardDetailItem.BoardContent(
                    id = board.boardId,
                    content = board.content,
                    images = board.images
                )
            )
            boardItem.add(
                GroupDetailBoardDetailItem.BoardTitle(
                    id = board.boardId,
                    title = "댓글",
                    boardCount = board.commentList?.size?.toString() ?: "0"
                )
            )
            board.commentList?.forEach { comment ->
                boardItem.add(
                    GroupDetailBoardDetailItem.BoardComment(
                        id = board.boardId,
                        commentId = comment.commentId,
                        userId = comment.userId,
                        userProfile = comment.userProfile,
                        userName = comment.userName,
                        userLocation = comment.userLocation,
                        timestamp = comment.timestamp,
                        isWriteOwner = (comment.userId == prefGetUserItem().let { userInfo ->
                            userInfo?.id
                        }),
                        comment = comment.comment
                    )
                )
                boardItem.add(
                    GroupDetailBoardDetailItem.BoardDivider(
                        id = board.boardId
                    )
                )
            }
        }
        _groupBoardItem.value = boardItem
    }

    /**
     * Firebase 댓글 데이터 추가 및 화면 출력
     */
    fun addBoardComment(
        itemPkId: String?,
        groupId: String?,
        userInfo: GroupUserInfoItem?,
        comment: String
    ) {
        if (itemPkId == null || groupId == null || userInfo == null) {
            return
        }
        //Firebase 댓글 데이터 추가
        viewModelScope.launch {
            runCatching {
                groupAddBoardCommentItem(
                    itemPkId,
                    groupId,
                    GroupDetailBoardDetailItem.BoardComment(
                        id = "newComment",
                        commentId = null,
                        userId = userInfo.userId,
                        userProfile = userInfo.userProfile,
                        userName = userInfo.userName,
                        userLocation = userInfo.userFirstLocation,
                        timestamp = System.currentTimeMillis(),
                        isWriteOwner = true,
                        comment = comment
                    )
                )
                _isSuccessAddComment.value = true
            }.onFailure {
                Log.e(TAG, it.message.toString())
                _isSuccessAddComment.value = false
            }
        }

        //현재 화면에 댓글 추가
        val currentItem = groupBoardItem.value.orEmpty().toMutableList()
        val currentTitleItem = currentItem.filterIsInstance<GroupDetailBoardDetailItem.BoardTitle>().firstOrNull()
        currentTitleItem?.let{
            currentItem[1] = it.copy(boardCount = (it.boardCount?.toInt()?.plus(1)).toString())
        }
        _groupBoardItem.value = currentItem.apply {
            add(
                GroupDetailBoardDetailItem.BoardComment(
                    id = "newComment",
                    commentId = null,
                    userId = userInfo.userId,
                    userProfile = userInfo.userProfile,
                    userName = userInfo.userName,
                    userLocation = userInfo.userFirstLocation.split(" ").last(),
                    timestamp = System.currentTimeMillis(),
                    isWriteOwner = true,
                    comment = comment
                )
            )
            add(
                GroupDetailBoardDetailItem.BoardDivider(
                    id = "newComment"
                )
            )
        }
    }

    /**
     * Firebase 댓글 데이터 삭제 및 화면 출력
     */
    fun deleteComment(
        itemPkId: String?,
        boardId: String?,
        commentId: String?,
        position: Int
    ) {
        if (itemPkId == null || boardId == null || commentId == null) {
            return
        }

        //Firebase 댓글 데이터 삭제
        viewModelScope.launch {
            runCatching {
                groupDeleteBoardCommentItem(
                    itemPkId,
                    boardId,
                    commentId
                )
            }.onFailure {
                Log.e(TAG, it.message.toString())
            }
        }

        //현재 화면에 댓글 삭제
        val currentItem = groupBoardItem.value.orEmpty().toMutableList()
        val currentTitleItem = currentItem.filterIsInstance<GroupDetailBoardDetailItem.BoardTitle>().firstOrNull()
        currentTitleItem?.let{
            currentItem[1] = it.copy(boardCount = (it.boardCount?.toInt()?.minus(1)).toString())
        }
        currentItem.removeAt(position)
        currentItem.removeAt(position) //Divider 도 같이 삭제
        _groupBoardItem.value = currentItem
    }
}

class GroupDetailBoardDetailViewModelFactory(
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
        val groupBoardCommentRepository =
            GroupRepositoryImpl(FirebaseDatabase.getInstance().getReference("group_list"))
        if (modelClass.isAssignableFrom(GroupDetailBoardDetailViewModel::class.java)) {
            return GroupDetailBoardDetailViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                GroupAddBoardCommentUseCase(groupBoardCommentRepository),
                GroupDeleteBoardCommentUseCase(groupBoardCommentRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}