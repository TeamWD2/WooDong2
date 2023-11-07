package com.wd.woodong2.presentation.group.detail.board.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.domain.usecase.GroupAddBoardCommentUseCase
import com.wd.woodong2.domain.usecase.GroupDeleteBoardCommentUseCase
import com.wd.woodong2.presentation.group.content.GroupItem
import kotlinx.coroutines.launch

class GroupDetailBoardDetailViewModel(
    private val groupAddBoardCommentItem: GroupAddBoardCommentUseCase,
    private val groupDeleteBoardCommentItem: GroupDeleteBoardCommentUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "GroupDetailBoardDetailViewModel"
    }

    private val _groupBoardItem: MutableLiveData<List<GroupDetailBoardDetailItem>> =
        MutableLiveData()
    val groupBoardItem: LiveData<List<GroupDetailBoardDetailItem>> get() = _groupBoardItem

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
        userId: String?,
        userProfile: String?,
        userName: String?,
        userLocation: String?,
        comment: String
    ) {
        if (itemPkId == null || groupId == null || userId == null || userName == null || userLocation == null) {
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
                        userId = userId,
                        userProfile = userProfile,
                        userName = userName,
                        userLocation = userLocation,
                        timestamp = System.currentTimeMillis(),
                        comment = comment
                    )
                )
            }.onFailure {
                Log.e(TAG, it.message.toString())
            }
        }

        //현재 화면에 댓글 추가
        val currentItem = groupBoardItem.value.orEmpty().toMutableList()
        _groupBoardItem.value = currentItem.apply {
            add(
                GroupDetailBoardDetailItem.BoardComment(
                    id = "newComment",
                    commentId = null,
                    userId = userId,
                    userProfile = userProfile,
                    userName = userName,
                    userLocation = userLocation,
                    timestamp = System.currentTimeMillis(),
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
        groupId: String?,
        commentId: String?,
        position: Int
    ) {
        if(itemPkId == null || groupId == null || commentId == null) {
            return
        }

        //Firebase 댓글 데이터 삭제
        viewModelScope.launch {
            runCatching {
                groupDeleteBoardCommentItem(
                    itemPkId,
                    groupId,
                    commentId
                )
            }.onFailure {
                Log.e(TAG, it.message.toString())
            }
        }

        //현재 화면에 댓글 삭제
        val currentItem = groupBoardItem.value.orEmpty().toMutableList()
        currentItem.removeAt(position)
        currentItem.removeAt(position) //Divider 도 같이 삭제
        _groupBoardItem.value = currentItem
    }
}

class GroupDetailBoardDetailViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository =
            GroupRepositoryImpl(FirebaseDatabase.getInstance().getReference("group_list"))
        if (modelClass.isAssignableFrom(GroupDetailBoardDetailViewModel::class.java)) {
            return GroupDetailBoardDetailViewModel(
                GroupAddBoardCommentUseCase(repository),
                GroupDeleteBoardCommentUseCase(repository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}