package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.model.UserItemsEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserItems(userId: String, entityResult: (UserItemsEntity?) -> Unit)
    fun updateUserLocations(userId: String, firstLocation : String, secondLocation : String)
    fun updateUserInfo(userId: String, name: String, imgProfile: String, email: String)
    suspend fun getUserItems(): Flow<UserItemsEntity?>
    suspend fun getUser(userId: String): Flow<UserEntity?>
    suspend fun addUser(user: UserEntity)
    suspend fun signUp(email: String, password: String, name: String): Flow<Boolean>
    suspend fun signIn(email: String, password: String): Flow<Boolean>
}