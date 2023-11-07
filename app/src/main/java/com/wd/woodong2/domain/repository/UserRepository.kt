package com.wd.woodong2.domain.repository

import android.net.Uri
import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.model.UserItemsEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun updateUserLocations(userId: String, firstLocation: String, secondLocation: String)
    fun updateUserInfo(userId: String, name: String, imgProfile: String, email: String)
    suspend fun updateUserToken(userId: String): Flow<Boolean>
    suspend fun getUserItems(): Flow<UserItemsEntity?>
    suspend fun getUser(userId: String): Flow<UserEntity?>
    fun addUser(user: UserEntity)
    suspend fun signUp(email: String, password: String, name: String, imgProfile: Uri?): Flow<Any>
    suspend fun signIn(email: String, password: String): Flow<Boolean>
    fun getUid(): String?
    suspend fun checkNicknameDup(nickname: String): Boolean
}