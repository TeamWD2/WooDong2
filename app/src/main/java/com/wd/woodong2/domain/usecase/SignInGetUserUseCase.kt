package com.wd.woodong2.domain.usecase

import android.util.Log
import com.wd.woodong2.domain.repository.UserPreferencesRepository

class SignInGetUserUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    operator fun invoke(): String? {

        Log.d("test", "${userPreferencesRepository.getUser()}")

        return userPreferencesRepository.getUser()
    }
}