package com.wd.woodong2.data.repository

import android.content.SharedPreferences
import com.wd.woodong2.domain.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : UserPreferencesRepository {

    override fun saveUser(userId: String, isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    override fun getUser(): String? {
        return sharedPreferences.getString("userId", null)
    }
}
