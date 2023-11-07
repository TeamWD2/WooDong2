package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.repository.GroupRepository
import com.wd.woodong2.presentation.group.detail.board.detail.GroupDetailBoardDetailItem

class GroupDeleteBoardCommentUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(
        itemId: String,
        groupId: String,
        commentId: String
    ) {
        return repository.deleteGroupBoardComment(itemId, groupId, commentId)
    }
}