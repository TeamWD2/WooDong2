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
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    override fun getUser(): String? {
        return if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            sharedPreferences.getString("userId", null)
        } else null
    }

    override fun deleteUser() {
        val editor = sharedPreferences.edit()
        editor.remove("userId")
        editor.remove("isLoggedIn")
        editor.apply()
    }
}
