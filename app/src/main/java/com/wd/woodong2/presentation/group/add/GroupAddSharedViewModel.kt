package com.wd.woodong2.presentation.group.add

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.data.repository.ImageStorageRepositoryImpl
import com.wd.woodong2.domain.usecase.GroupSetItemUseCase
import com.wd.woodong2.domain.usecase.ImageStorageSetItemUseCase
import kotlinx.coroutines.launch
import java.util.UUID

class GroupAddSharedViewModel(
    private val imageStorageSetItem: ImageStorageSetItemUseCase,
    private val groupSetItem: GroupSetItemUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "GroupAddSharedViewModel"
    }

    private val _viewPager2CurItem: MutableLiveData<Int> = MutableLiveData()
    val viewPager2CurItem: LiveData<Int> = _viewPager2CurItem

    private val groupAddMain: MutableLiveData<GroupAddSetItem.GroupAddMain> =
        MutableLiveData(GroupAddSetItem.GroupAddMain())
    private val groupAddIntroduce: MutableLiveData<GroupAddSetItem.GroupAddIntroduce> =
        MutableLiveData(GroupAddSetItem.GroupAddIntroduce())

    private val _mainImage: MutableLiveData<Uri> = MutableLiveData()
    val mainImage: LiveData<Uri> get() = _mainImage

    private val _backgroundImage: MutableLiveData<Uri> = MutableLiveData()
    val backgroundImage: LiveData<Uri> get() = _backgroundImage

    private val _isCreateSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isCreateSuccess: LiveData<Boolean> get() = _isCreateSuccess

    fun modifyViewPager2() {
        _viewPager2CurItem.value = viewPager2CurItem.value?.plus(1)
    }

    fun setItem(textName: String, text: String?) {
        when(textName) {
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


    fun setImage(currentItem: String?, image: Uri) = viewModelScope.launch {
        runCatching {
            imageStorageSetItem(image).collect { imageUri ->
                when(currentItem) {
                    "imgMainImage" -> {
                        _mainImage.value = imageUri
                        groupAddMain.value = groupAddMain.value?.copy(mainImage = imageUri.toString())
                    }
                    "imgBackgroundImage" -> {
                        _backgroundImage.value = imageUri
                        groupAddMain.value =
                            groupAddMain.value?.copy(backgroundImage = imageUri.toString())
                    }
                }
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
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

    private fun combineGroupItem(): List<GroupAddSetItem> =
        mutableListOf<GroupAddSetItem>().apply {
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
                            "-NhImSiData",
                            "https://i.ytimg.com/vi/dhZH7NLCOmk/default.jpg",
                            "sinw",
                            "권선동",
                            "(방장)"
                        )
                    )
                )
            )
        }
}

class GroupAddSharedViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val imageStorageRepository =
            ImageStorageRepositoryImpl(FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}"))
        val groupRepository =
            GroupRepositoryImpl(FirebaseDatabase.getInstance().getReference("group_list"))
        if (modelClass.isAssignableFrom(GroupAddSharedViewModel::class.java)) {
            return GroupAddSharedViewModel(
                ImageStorageSetItemUseCase(imageStorageRepository),
                GroupSetItemUseCase(groupRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}