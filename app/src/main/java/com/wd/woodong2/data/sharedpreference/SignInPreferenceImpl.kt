package com.wd.woodong2.data.sharedpreference

import android.content.SharedPreferences
import com.wd.woodong2.data.localsource.SignInPreference

class SignInPreferenceImpl(
    private val sharedPreferences: SharedPreferences,
) : SignInPreference {
    companion object {
        const val TAG = "UserPref"

        const val USER_ID = "userId"
        const val IS_LOGGED_IN = "isLoggedIn"
    }

    override fun saveUser(userId: String, isLoggedIn: Boolean) {
        sharedPreferences.edit().apply {
            putString(USER_ID, userId)
            putBoolean(IS_LOGGED_IN, isLoggedIn)
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