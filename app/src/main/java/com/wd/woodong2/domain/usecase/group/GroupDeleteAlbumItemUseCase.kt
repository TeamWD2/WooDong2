package com.wd.woodong2.domain.usecase.group

import com.wd.woodong2.domain.repository.GroupRepository

class GroupDeleteAlbumItemUseCase(
    private val repository: GroupRepository
) {
    operator fun invoke(
        itemId: String,
        imageList: List<String>
    ) {
        return repository.deleteGroupAlbumItem(itemId, imageList)
    }
}