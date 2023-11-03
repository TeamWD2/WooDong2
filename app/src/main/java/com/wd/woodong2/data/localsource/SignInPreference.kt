package com.wd.woodong2.data.localsource

/*
* 자동 로그인 preference
* */
interface SignInPreference {
    fun saveUser(userId: String, isLoggedIn: Boolean)
    fun getUser(): String?
    fun deleteUser()
}