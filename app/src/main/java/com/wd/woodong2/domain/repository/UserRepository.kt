package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.UserItemsEntity

interface UserRepository {
    fun getUserItems(entityResult: (UserItemsEntity?) -> Unit)
}