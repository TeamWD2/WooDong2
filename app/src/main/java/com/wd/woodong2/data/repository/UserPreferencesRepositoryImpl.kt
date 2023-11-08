package com.wd.woodong2.data.repository

import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.repository.SignInPreference
import com.wd.woodong2.domain.repository.UserInfoPreference
import com.wd.woodong2.domain.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(
    private val signinPref: SignInPreference?,
    private val userInfoPref: UserInfoPreference,
) : UserPreferencesRepository {

    companion object {
        const val TAG = "UserPreferencesRepository"
    }

    /**
     * SignInPreference
     * */
    override fun saveUser(userId: String, isLoggedIn: Boolean, uid: String) {
        signinPref?.saveUser(userId, isLoggedIn, uid)
    }

    override fun getUID(): String? {
        return signinPref?.getUID()
    }

    override fun deleteUser() {
        signinPref?.deleteUser()
        userInfoPref.deleteUser()
    }

    /**
     * UserInfoPreference
     * */
    override fun setUserInfo(user: UserEntity) {
        userInfoPref.setUserInfo(user)
    }

    override fun getUserInfo(): UserEntity? {
        return userInfoPref.getUserInfo()
    }
}
