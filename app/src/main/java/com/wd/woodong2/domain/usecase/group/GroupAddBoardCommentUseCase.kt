package com.wd.woodong2.domain.usecase.group

import com.wd.woodong2.domain.repository.GroupRepository
import com.wd.woodong2.presentation.group.detail.board.detail.GroupDetailBoardDetailItem

class GroupAddBoardCommentUseCase(
    private val repository: GroupRepository
) {
    operator fun invoke(
        itemId: String,
        groupId: String,
        boardComment: GroupDetailBoardDetailItem.BoardComment
    ) {
        return repository.addGroupBoardComment(itemId, groupId, boardComment)
    }
}