package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.GroupItemsEntity

interface GroupRepository {
    fun getGroupItems(entityResult: (GroupItemsEntity?) -> Unit)
}