package com.wd.woodong2.domain.usecase

import android.util.Log
import com.wd.woodong2.domain.model.UserItemsEntity
import com.wd.woodong2.domain.repository.UserRepository

class UserGetItemsUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(userId: String, entityResult: (UserItemsEntity?) -> Unit) {
        return repository.getUserItems(userId) { result ->
            entityResult(result)
        }
    }
    operator fun invoke(userId: String, firstLocation: String, secondLocation: String) {
        Log.d("location", "firstLocationx")
        return repository.updateUserLocations(userId, firstLocation, secondLocation)
    }
    operator fun invoke(userId: String, name: String, imgProfile: String, email: String){
        return repository.updateUserInfo(userId, name, imgProfile, email)
    }

}