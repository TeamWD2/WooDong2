package com.wd.woodong2.domain.repository

interface UserPreferencesRepository {
    fun saveUser(userId: String, isLoggedIn: Boolean, uid: String)
    fun getUID(): String?
    fun deleteUser()
}
