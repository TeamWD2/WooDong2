package com.wd.woodong2.domain.repository

/*
* 자동 로그인 preference
* */
interface SignInPreference {
    fun saveUser(userId: String, isLoggedIn: Boolean, uid: String)
    fun getUID(): String?
    fun deleteUser()
}