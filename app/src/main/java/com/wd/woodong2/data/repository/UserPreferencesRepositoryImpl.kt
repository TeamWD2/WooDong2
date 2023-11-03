package com.wd.woodong2.data.repository

import com.wd.woodong2.data.localsource.SignInPreference
import com.wd.woodong2.domain.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val userPref: SignInPreference,
) : UserPreferencesRepository {

    companion object {
        const val TAG = "UserPreferencesRepository"
    }

    override fun saveUser(userId: String, isLoggedIn: Boolean) {
        userPref.saveUser(userId, isLoggedIn)
    }

    override fun getUser(): String? {
        return userPref.getUser()
    }

    override fun deleteUser() {
        return userPref.deleteUser()
    }
}
