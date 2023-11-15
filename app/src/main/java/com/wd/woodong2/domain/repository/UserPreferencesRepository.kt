package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.UserEntity

interface UserPreferencesRepository {
    fun saveUser(userId: String, isLoggedIn: Boolean, uid: String)
    fun setUserInfo(user: UserEntity)
    fun getUserInfo(): UserEntity?
    fun getUID(): String?
    fun deleteUser()
    fun editUserInfo(name: String?, profile: String?,firstLocation: String?, secondLocation: String?): UserEntity?
}
