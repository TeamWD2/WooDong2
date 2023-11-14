package com.wd.woodong2.presentation.home.add

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.wd.woodong2.R
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.MapSearchCircumLocationGetItemsUseCase
import com.wd.woodong2.domain.usecase.MapSearchGetItemsUseCase
import com.wd.woodong2.domain.usecase.UserAddIdsUseCase
import com.wd.woodong2.domain.usecase.UserGetItemUseCase
import com.wd.woodong2.domain.usecase.UserPrefGetItemUseCase
import com.wd.woodong2.domain.usecase.UserUpdateInfoUseCase
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.group.GroupUserInfoItem
import com.wd.woodong2.presentation.home.content.HomeViewModel
import kotlinx.coroutines.launch
import java.util.UUID


class HomeAddViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val userAddIdsUseCase: UserAddIdsUseCase
) : ViewModel() {

    private val databaseReference = FirebaseDatabase.getInstance().reference.child("home_list")
    private val storageReference = FirebaseStorage.getInstance().reference

    fun uploadData(
        id: String?,
        name: String?,
        selectedTag: String?,
        groupTag: String?,
        selectedImageUri: Uri?,
        thumbnailCount: Int?,
        title: String,
        description: String,
        location: String?,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            if (selectedImageUri != null) {
                val storageRef = storageReference.child("images/${UUID.randomUUID()}")
                storageRef.putFile(selectedImageUri).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        val newRef = databaseReference.push()
                        val newItemId = newRef.key ?: ""
                        val data = HomeAddItem(
                            newItemId,
                            name = name.toString(),
                            tag = selectedTag,
                            groupTag = groupTag.toString(),
                            thumbnail = imageUrl.toString(),
                            thumbnailCount = thumbnailCount!!,
                            title = title,
                            description = description,
                            location = location.toString(),
                        )

                        userAddIdsUseCase(getUserInfo()?.id ?: "UserId", newItemId, null)
                        newRef.setValue(data)
                        onComplete()
                    }
                }
            } else {
                val newRef = databaseReference.push()
                val newItemId = newRef.key ?: ""
                val data = HomeAddItem(
                    id = newItemId,
                    name = name.toString(),
                    tag = selectedTag,
                    groupTag = groupTag.toString(),
                    thumbnailCount = thumbnailCount!!,
                    title = title,
                    description = description,
                    location = location.toString(),
                )

                userAddIdsUseCase(getUserInfo()?.id ?: "UserId", newItemId, null)
                newRef.setValue(data)
                onComplete()
            }
        }
    }

    fun getUserInfo() =
        prefGetUserItem()?.let {
            UserItem(
                id = it.id ?: "unknown",
                name = it.name ?: "unknown",
                imgProfile = it.imgProfile,
                email = it.email ?: "unknown",
                chatIds = it.chatIds,
                groupIds = it.groupIds,
                likedIds = it.likedIds,
                writtenIds = it.writtenIds,
                firstLocation = it.firstLocation ?: "unknown",
                secondLocation = it.secondLocation ?: "unknown"
            )
        }
}

class HomeAddViewModelFactory(
    val context: Context
) : ViewModelProvider.Factory {
    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)
    private val databaseReference = FirebaseDatabase.getInstance()

    val userPrefRepository = UserPreferencesRepositoryImpl(
        null,
        UserInfoPreferenceImpl(
            context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
        )
    )

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("user_list"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeAddViewModel::class.java)) {
            return HomeAddViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                UserAddIdsUseCase(userRepositoryImpl),

                ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}
