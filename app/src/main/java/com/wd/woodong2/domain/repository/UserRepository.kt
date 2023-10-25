package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.UserItemsEntity

interface UserRepository {
    fun getUserItems(userId: String, entityResult: (UserItemsEntity?) -> Unit)
    fun updateUserLocations(userId: String, firstLocation : String, secondLocation : String)

}