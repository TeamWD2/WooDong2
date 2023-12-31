package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.UserEntity

interface UserInfoPreference {
    fun setUserInfo(user: UserEntity)
    fun getUserInfo(): UserEntity?
    fun deleteUser()
    fun editUserInfo(name: String?, profile: String?,firstLocation: String?, secondLocation: String?): UserEntity
}