package com.wd.woodong2.data.sharedpreference

import android.content.SharedPreferences
import com.google.gson.Gson
import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.repository.UserInfoPreference

class UserInfoPreferenceImpl(
    private val sharedPreferences: SharedPreferences,
) : UserInfoPreference {
    companion object {
        const val USER_INFO = "user_info"
    }

    override fun setUserInfo(user: UserEntity) {
        val gson = Gson()
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString(USER_INFO, userJson).apply()
    }

    override fun getUserInfo(): UserEntity? {
        val gson = Gson()
        val userJson = sharedPreferences.getString(USER_INFO, "")
        return gson.fromJson(userJson, UserEntity::class.java)
    }

    override fun deleteUser() {
        sharedPreferences.edit().apply {
            remove(USER_INFO)
            apply()
        }
    }
    override fun editUserInfo(name: String?, profile: String?,firstLocation: String?, secondLocation: String?): UserEntity{
        val gson = Gson()
        val userJson = sharedPreferences.getString(USER_INFO, "")
        val userInfo = gson.fromJson(userJson, UserEntity::class.java)

        val newUserInfo = userInfo.copy(
            name = name,
            imgProfile = profile,
            firstLocation = firstLocation,
            secondLocation = secondLocation
        )
        val editor = sharedPreferences.edit()
        editor.putString(USER_INFO, gson.toJson(newUserInfo))
        editor.apply()
        return newUserInfo
    }
}