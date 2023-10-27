package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.model.UserItemsEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserItems(): Flow<UserItemsEntity?>
    suspend fun getUser(userId: String): Flow<UserEntity?>
    suspend fun addUser(user: UserEntity)
    suspend fun signUp(email: String, password: String, name: String): Flow<Boolean>
    suspend fun signIn(email: String, password: String): Flow<Boolean>
}