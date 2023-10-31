package com.wd.woodong2.data.repository

import android.content.SharedPreferences
import com.wd.woodong2.domain.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : UserPreferencesRepository {

    companion object {
        const val TAG = "UserPreferencesRepository"
    }

    override fun saveUser(userId: String, isLoggedIn: Boolean) {
        sharedPreferences.edit().apply {
            putString("userId", userId)
            putBoolean("isLoggedIn", isLoggedIn)
        }.apply()
    }

    override fun getUser(): String? {
        return if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            sharedPreferences.getString("userId", null)
        } else null
    }

    override fun deleteUser() {
        sharedPreferences.edit().apply {
            remove("userId")
            remove("isLoggedIn")
            apply()
        }
    }
}
