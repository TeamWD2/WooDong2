package com.wd.woodong2.presentation.group.detail.board.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wd.woodong2.presentation.group.content.GroupItem

class GroupDetailBoardDetailViewModel : ViewModel() {
    private val _groupBoardItem: MutableLiveData<List<GroupDetailBoardDetailItem>> = MutableLiveData()
    val groupBoardItem: LiveData<List<GroupDetailBoardDetailItem>> get() = _groupBoardItem

    fun setGroupBoardItem(groupBoardItem: GroupItem.GroupBoard?) {
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
                }
            }
        }
        _groupBoardItem.value = boardItem
    }
}