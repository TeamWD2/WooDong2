package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.model.UserItemsEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun updateUserPassword(email: String, currentPassword: String, newPassword: String): Flow<Boolean>
    fun updateUserInfo(userId: String, imgProfile: String, name: String, firstLocation: String, secondLocation: String)
    suspend fun addUserIds(userId: String,writtenId: String?, groupId: String?, likedId: String?): Flow<UserEntity?>
    suspend fun getUserItems(): Flow<UserItemsEntity?>
    suspend fun getUser(userId: String): Flow<UserEntity?>
    suspend fun addUser(user: UserEntity)
    suspend fun signUp(email: String, password: String, name: String): Flow<Boolean>
    suspend fun signIn(email: String, password: String): Flow<Boolean>
}