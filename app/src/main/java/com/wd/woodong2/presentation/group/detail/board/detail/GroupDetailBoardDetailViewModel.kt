package com.wd.woodong2.presentation.group.detail.board.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wd.woodong2.presentation.group.content.GroupItem

class GroupDetailBoardDetailViewModel : ViewModel() {
    private val _groupBoardItem: MutableLiveData<List<GroupDetailBoardDetailItem>> = MutableLiveData()
    val groupBoardItem: LiveData<List<GroupDetailBoardDetailItem>> get() = _groupBoardItem

    /**
     * 넘겨받아온 데이터 화면에 출력하기 위해 ViewType 별로 가공
     */
    fun initGroupBoardItem(groupBoardItem: GroupItem.GroupBoard?) {
        val boardItem = mutableListOf<GroupDetailBoardDetailItem>()
        groupBoardItem?.let { board ->
            board.boardList?.let { boardList ->
                for(item in boardList) {
                    boardItem.add(
                        GroupDetailBoardDetailItem.BoardContent(
                            id = board.id,
                            content = item.content,
                            images = item.images
                        )
                    )
                    boardItem.add(
                        GroupDetailBoardDetailItem.BoardTitle(
                            id = board.id,
                            title = "댓글",
                            boardCount = "0" //임시
                        )
                    )
                    //Todo("Comment 관련 데이터 추가 - BoardComment + BoardDivider")
                }
            }
        }
        _groupBoardItem.value = boardItem
    }

    /**
     * Firebase 댓글 데이터 추가 및 화면 출력
     */
    fun addBoardComment(
        userId: String?,
        userProfile: String?,
        userName: String?,
        userLocation: String?,
        comment: String
    ) {
        if(userId == null || userName == null || userLocation == null) {
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
    fun deleteComment(position: Int) {
        //Firebase 댓글 데이터 삭제
        //Todo("Firebase 댓글 데이터 삭제")

        //현재 화면에 댓글 삭제
        val currentItem = groupBoardItem.value.orEmpty().toMutableList()
        currentItem.removeAt(position)
        currentItem.removeAt(position) //Divider 도 같이 삭제
        _groupBoardItem.value = currentItem
    }
}