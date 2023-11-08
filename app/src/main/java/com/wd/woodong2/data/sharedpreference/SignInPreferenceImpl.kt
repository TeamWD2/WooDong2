package com.wd.woodong2.data.sharedpreference

import android.content.SharedPreferences
import com.wd.woodong2.domain.repository.SignInPreference

class SignInPreferenceImpl(
    private val sharedPreferences: SharedPreferences,
) : SignInPreference {
    companion object {
        const val USER_ID = "userId"
        const val USER_UID = "userUID"
        const val IS_LOGGED_IN = "isLoggedIn"
    }

    override fun saveUser(userId: String, isLoggedIn: Boolean, uid: String) {
        sharedPreferences.edit().apply {
            putString(USER_ID, userId)
            putString(USER_UID, uid)
            putBoolean(IS_LOGGED_IN, isLoggedIn)
        }.apply()
    }

    override fun getUID(): String? {
        return if (sharedPreferences.getBoolean(IS_LOGGED_IN, false) &&
            sharedPreferences.getString(USER_ID, null) != null
        ) {
            sharedPreferences.getString(USER_UID, null)
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