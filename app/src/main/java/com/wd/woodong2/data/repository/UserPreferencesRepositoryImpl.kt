package com.wd.woodong2.data.repository

import com.wd.woodong2.domain.repository.SignInPreference
import com.wd.woodong2.domain.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val signinPref: SignInPreference,
) : UserPreferencesRepository {

    companion object {
        const val TAG = "UserPreferencesRepository"
    }

    override fun saveUser(userId: String, isLoggedIn: Boolean, uid: String) {
        signinPref.saveUser(userId, isLoggedIn, uid)
    }

    override fun getUID(): String? {
        return signinPref.getUID()
    }

    override fun deleteUser() {
        return signinPref.deleteUser()
    }
}
