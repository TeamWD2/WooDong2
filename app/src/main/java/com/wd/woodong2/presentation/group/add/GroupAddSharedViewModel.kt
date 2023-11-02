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

    private val groupAddMain: MutableLiveData<GroupAddSetItem.GroupAddMain> =
        MutableLiveData(GroupAddSetItem.GroupAddMain())
    private val groupAddIntroduce: MutableLiveData<GroupAddSetItem.GroupAddIntroduce> =
        MutableLiveData(GroupAddSetItem.GroupAddIntroduce())

    private val _isCreateSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isCreateSuccess: LiveData<Boolean> get() = _isCreateSuccess

    fun setGroupTag(groupTag: String?) {
        groupAddMain.value = groupAddMain.value?.copy(groupTag = groupTag)
        groupAddIntroduce.value = groupAddIntroduce.value?.copy(groupTag = groupTag)
    }

    fun setGroupName(groupName: String) {
        groupAddMain.value = groupAddMain.value?.copy(groupName = groupName)
    }

    fun setGroupIntroduce(groupIntro: String) {
        groupAddMain.value = groupAddMain.value?.copy(introduce = groupIntro)
        groupAddIntroduce.value = groupAddIntroduce.value?.copy(introduce = groupIntro)
    }

    fun setAgeLimit(ageLimit: String?) {
        groupAddMain.value = groupAddMain.value?.copy(ageLimit = ageLimit)
        groupAddIntroduce.value = groupAddIntroduce.value?.copy(ageLimit = ageLimit)
    }

    fun setMemberLimit(memberLimit: String?) {
        groupAddMain.value = groupAddMain.value?.copy(memberLimit = memberLimit)
        groupAddIntroduce.value = groupAddIntroduce.value?.copy(memberLimit = memberLimit)
    }

    fun setPassword(password: String?) {
        groupAddMain.value = groupAddMain.value?.copy(password = password)
    }

    fun setMainImage(mainImage: Uri) = viewModelScope.launch {
        runCatching {
            imageStorageSetItem(mainImage).collect { imageUri ->
                groupAddMain.value = groupAddMain.value?.copy(mainImage = imageUri.toString())
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
        }
    }

    fun setBackgroundImage(backgroundImage: Uri) = viewModelScope.launch {
        runCatching {
            imageStorageSetItem(backgroundImage).collect { imageUri ->
                groupAddMain.value =
                    groupAddMain.value?.copy(backgroundImage = imageUri.toString())
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