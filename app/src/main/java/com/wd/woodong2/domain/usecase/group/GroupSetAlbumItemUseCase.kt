package com.wd.woodong2.domain.usecase.group

import com.wd.woodong2.domain.repository.GroupRepository
import com.wd.woodong2.presentation.group.detail.board.add.GroupDetailBoardAddItem

class GroupSetAlbumItemUseCase(
    private val repository: GroupRepository
) {
    operator fun invoke(itemId: String, groupAlbumItems: List<String>) {
        return repository.setGroupAlbumItem(itemId, groupAlbumItems)
    }
}