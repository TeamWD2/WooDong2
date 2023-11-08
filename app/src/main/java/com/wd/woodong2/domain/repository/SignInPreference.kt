package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.UserEntity

/*
* 자동 로그인 preference
* */
interface SignInPreference {
    fun saveUser(userId: String, isLoggedIn: Boolean, uid: String)
    fun getUID(): String?
    fun deleteUser()
}