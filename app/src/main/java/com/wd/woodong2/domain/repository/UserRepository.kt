package com.wd.woodong2.domain.repository

import android.net.Uri
import com.wd.woodong2.domain.model.GroupMemberItemEntity
import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.model.UserItemsEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun updateUserPassword(email: String, currentPassword: String, newPassword: String)
    fun updateUserInfo(
        userId: String,
        imgProfile: String,
        name: String,
        firstLocation: String,
        secondLocation: String,
    )

    fun addUserIds(userId: String, writtenId: String?, likedId: String?)
    fun removeUserIds(
        userId: String,
        writtenId: String?,
        likedId: String?,
        groupId: String?,
        chatId: String?,
    )

    fun updateUserToken(userId: String)
    suspend fun getUserItems(): Flow<UserItemsEntity?>
    suspend fun getUser(userId: String): Flow<UserEntity?>
    fun addUser(user: UserEntity)
    suspend fun signUp(email: String, password: String, name: String, imgProfile: Uri?): Flow<Any>
    suspend fun signIn(email: String, password: String): Flow<Boolean>
    fun getUid(): String?
    suspend fun checkNicknameDup(nickname: String): Boolean
    suspend fun updateGroupInfo(userId: String, groupId: String?, chatId: String?)
    suspend fun sendPushMessageToGroupMember(memberList: List<GroupMemberItemEntity>?)
    fun logout()
    suspend fun deleteUser(userId: String)
}