package com.wd.woodong2.data.repository

import android.content.SharedPreferences
import com.wd.woodong2.domain.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : UserPreferencesRepository {

    companion object {
        const val TAG = "UserPreferencesRepository"

        const val USER_ID = "userId"
        const val IS_LOGGED_IN = "isLoggedIn"
        const val TOKEN = "token"
    }

    override fun saveUser(userId: String, isLoggedIn: Boolean, token: String) {
        sharedPreferences.edit().apply {
            putString(USER_ID, userId)
            putBoolean(IS_LOGGED_IN, isLoggedIn)
            putString(TOKEN, token)
        }.apply()
    }

    override fun getUser(): String? {
        return if (sharedPreferences.getBoolean(IS_LOGGED_IN, false)) {
            sharedPreferences.getString(USER_ID, null)
        } else null
    }

    override fun deleteUser() {
        sharedPreferences.edit().apply {
            remove(USER_ID)
            remove(IS_LOGGED_IN)
            apply()
        }
    }
}
