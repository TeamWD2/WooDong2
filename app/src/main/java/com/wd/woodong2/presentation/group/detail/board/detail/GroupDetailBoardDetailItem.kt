package com.wd.woodong2.presentation.group.detail.board.detail

sealed class GroupDetailBoardDetailItem(
    open val id: String?
) {
    data class BoardContent(
        override val id: String?,
        val content: String?,
        val images: List<String>?
    ) : GroupDetailBoardDetailItem(id)

    data class BoardTitle(
        override val id: String?,
        val title: String?,
        val boardCount: String?
    ) : GroupDetailBoardDetailItem(id)

    data class BoardComment(
        override val id: String?,
        val commentId: String?,
        val userId: String?,
        val userProfile: String?,
        val userName: String?,
        val userLocation: String?,
        val timestamp: Long?,
        val isWriteOwner: Boolean?,
        val comment: String?
    ) : GroupDetailBoardDetailItem(id)

    data class BoardDivider(
        override val id: String?
    ) : GroupDetailBoardDetailItem(id)
}