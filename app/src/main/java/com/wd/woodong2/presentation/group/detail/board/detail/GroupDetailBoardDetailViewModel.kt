package com.wd.woodong2.presentation.group.detail.board.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wd.woodong2.presentation.group.content.GroupItem

class GroupDetailBoardDetailViewModel : ViewModel() {
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
                    boardCount = "0" //임시
                )
            )
            //Todo("Comment 관련 데이터 추가 - BoardComment + BoardDivider")
        }
        _groupBoardItem.value = boardItem
    }

    /**
     * Firebase 댓글 데이터 추가 및 화면 출력
     */
    fun addBoardComment(
        groupPkId: String?,
        userId: String?,
        userProfile: String?,
        userName: String?,
        userLocation: String?,
        comment: String
    ) {
        if (groupPkId == null || userId == null || userName == null || userLocation == null) {
            return
        }
        //Firebase 댓글 데이터 추가
        //Todo("Firebase 댓글 데이터 추가")

        //현재 화면에 댓글 추가
        val currentItem = groupBoardItem.value.orEmpty().toMutableList()
        _groupBoardItem.value = currentItem.apply {
            add(
                GroupDetailBoardDetailItem.BoardComment(
                    id = "newComment",
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
        groupPkId: String?,
        position: Int
    ) {
        if(groupPkId == null) {
            return
        }

        //Firebase 댓글 데이터 삭제
        //Todo("Firebase 댓글 데이터 삭제")

        //현재 화면에 댓글 삭제
        val currentItem = groupBoardItem.value.orEmpty().toMutableList()
        currentItem.removeAt(position)
        currentItem.removeAt(position) //Divider 도 같이 삭제
        _groupBoardItem.value = currentItem
    }
}