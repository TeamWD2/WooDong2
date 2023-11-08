package com.wd.woodong2.presentation.group.add

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.wd.woodong2.R
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.data.repository.ImageStorageRepositoryImpl
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.usecase.GroupSetItemUseCase
import com.wd.woodong2.domain.usecase.ImageStorageSetItemUseCase
import com.wd.woodong2.domain.usecase.UserPrefGetItemUseCase
import com.wd.woodong2.presentation.group.GroupUserInfoItem
import kotlinx.coroutines.launch
import java.util.UUID

class GroupAddSharedViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val imageStorageSetItem: ImageStorageSetItemUseCase,
    private val groupSetItem: GroupSetItemUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "GroupAddSharedViewModel"
    }

    private val _viewPager2CurItem: MutableLiveData<Int> = MutableLiveData(0)
    val viewPager2CurItem: LiveData<Int> = _viewPager2CurItem

    private val groupAddMain: MutableLiveData<GroupAddSetItem.GroupAddMain> =
        MutableLiveData(GroupAddSetItem.GroupAddMain())
    private val groupAddIntroduce: MutableLiveData<GroupAddSetItem.GroupAddIntroduce> =
        MutableLiveData(GroupAddSetItem.GroupAddIntroduce())

    private val _isCreateSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isCreateSuccess: LiveData<Boolean> get() = _isCreateSuccess

    fun modifyViewPager2(count: Int) {
        _viewPager2CurItem.value = viewPager2CurItem.value?.plus(count)
    }

    fun getUserInfo() =
        prefGetUserItem()?.let {
            GroupUserInfoItem(
                userId = it.id ?: "(알 수 없음)",
                userProfile = it.imgProfile,
                userName = it.name ?: "(알 수 없음)",
                userLocation = it.firstLocation ?: "(알 수 없음)"
            )
        }

    fun setItem(textName: String, text: String?) {
        when (textName) {
            "groupTag" -> {
                groupAddMain.value = groupAddMain.value?.copy(groupTag = text)
                groupAddIntroduce.value = groupAddIntroduce.value?.copy(groupTag = text)
            }

            "groupName" -> {
                groupAddMain.value = groupAddMain.value?.copy(groupName = text)
            }

            "groupIntro" -> {
                groupAddMain.value = groupAddMain.value?.copy(introduce = text)
                groupAddIntroduce.value = groupAddIntroduce.value?.copy(introduce = text)
            }

            "ageLimit" -> {
                groupAddMain.value = groupAddMain.value?.copy(ageLimit = text)
                groupAddIntroduce.value = groupAddIntroduce.value?.copy(ageLimit = text)
            }

            "memberLimit" -> {
                groupAddMain.value = groupAddMain.value?.copy(memberLimit = text)
                groupAddIntroduce.value = groupAddIntroduce.value?.copy(memberLimit = text)
            }

            "password" -> {
                groupAddMain.value = groupAddMain.value?.copy(password = text)
            }
        }
    }

    fun setImage(currentItem: String?, image: Uri) {
        when (currentItem) {
            "imgMainImage" -> {
                groupAddMain.value = groupAddMain.value?.copy(mainImage = image.toString())
            }

            "imgBackgroundImage" -> {
                groupAddMain.value =
                    groupAddMain.value?.copy(backgroundImage = image.toString())
            }
        }
    }

    fun setGroupAddItem() {
        if (isCorrectGroupAddItem()) {
            viewModelScope.launch {
                runCatching {
                    groupSetItem(combineGroupItem())
                    _isCreateSuccess.value = true
                }.onFailure {
                    Log.e(TAG, it.message.toString())
                }
            }
        } else {
            _isCreateSuccess.value = false
        }
    }

    private fun isCorrectGroupAddItem(): Boolean =
        isCorrectMain(groupAddMain.value) && isCorrectIntroduce(groupAddIntroduce.value)

    private fun isCorrectMain(groupAddMain: GroupAddSetItem.GroupAddMain?): Boolean =
        groupAddMain?.let {
            it.groupName.isNullOrBlank().not()
                    && it.introduce.isNullOrBlank().not()
                    && it.groupTag.isNullOrBlank().not()
                    && it.ageLimit.isNullOrBlank().not()
                    && it.memberLimit.isNullOrBlank().not()
                    && it.password.isNullOrBlank().not()
                    && it.mainImage.isNullOrBlank().not()
                    && it.backgroundImage.isNullOrBlank().not()
        } ?: false

    private fun isCorrectIntroduce(groupAddIntroduce: GroupAddSetItem.GroupAddIntroduce?): Boolean =
        groupAddIntroduce?.let {
            it.introduce.isNullOrBlank().not()
                    && it.groupTag.isNullOrBlank().not()
                    && it.ageLimit.isNullOrBlank().not()
                    && it.memberLimit.isNullOrBlank().not()
        } ?: false

    private fun combineGroupItem(): List<GroupAddSetItem> {
        getImageStorage("imgMainImage", Uri.parse(groupAddMain.value?.mainImage))
        getImageStorage("imgBackgroundImage", Uri.parse(groupAddMain.value?.backgroundImage))
        return mutableListOf<GroupAddSetItem>().apply {
            groupAddMain.value?.let {
                add(it)
            }
            groupAddIntroduce.value?.let {
                add(it)
            }
            add(
                GroupAddSetItem.GroupAddMember(
                    memberList = listOf(
                        GroupAddSetItem.AddMember(
                            getUserInfo()?.userId,
                            getUserInfo()?.userProfile,
                            getUserInfo()?.userName,
                            getUserInfo()?.userLocation,
                            "(방장)"
                        )
                    )
                )
            )
            add(GroupAddSetItem.GroupAddBoard())
            add(GroupAddSetItem.GroupAddAlbum())
        }
    }

    private fun getImageStorage(currentItem: String, image: Uri) = viewModelScope.launch {
        runCatching {
            imageStorageSetItem(image).collect { imageUri ->
                when(currentItem) {
                    "imgMainImage" -> groupAddMain.value = groupAddMain.value?.copy(mainImage = imageUri.toString())
                    "imgBackgroundImage" -> groupAddMain.value = groupAddMain.value?.copy(backgroundImage = imageUri.toString())
                }
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
        }
    }
}

class GroupAddSharedViewModelFactory(
    val context: Context
) : ViewModelProvider.Factory {
    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val userPrefRepository = UserPreferencesRepositoryImpl(
            null,
            UserInfoPreferenceImpl(
                context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
            )
        )
        val imageStorageRepository =
            ImageStorageRepositoryImpl(FirebaseStorage.getInstance().reference.child("images/groupList/${UUID.randomUUID()}"))
        val groupSetRepository =
            GroupRepositoryImpl(FirebaseDatabase.getInstance().getReference("group_list"))
        if (modelClass.isAssignableFrom(GroupAddSharedViewModel::class.java)) {
            return GroupAddSharedViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                ImageStorageSetItemUseCase(imageStorageRepository),
                GroupSetItemUseCase(groupSetRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}