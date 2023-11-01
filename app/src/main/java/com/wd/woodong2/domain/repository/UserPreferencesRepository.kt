package com.wd.woodong2.domain.repository

interface UserPreferencesRepository {
    fun saveUser(userId: String, isLoggedIn: Boolean)
    fun getUser(): String?
    fun deleteUser()
}