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
import com.wd.woodong2.data.repository.ChatRepositoryImpl
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.data.repository.ImageStorageRepositoryImpl
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.usecase.ChatSetItemUseCase
import com.wd.woodong2.domain.usecase.GroupSetItemUseCase
import com.wd.woodong2.domain.usecase.GroupSetMemberItemUseCase
import com.wd.woodong2.domain.usecase.ImageStorageSetItemUseCase
import com.wd.woodong2.domain.usecase.UserPrefGetItemUseCase
import com.wd.woodong2.domain.usecase.UserUpdateGroupInfoUseCase
import com.wd.woodong2.presentation.group.GroupUserInfoItem
import com.wd.woodong2.presentation.group.detail.GroupDetailChatItem
import com.wd.woodong2.presentation.group.detail.GroupDetailMemberAddItem
import kotlinx.coroutines.launch
import java.util.UUID

class GroupAddSharedViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val imageStorageSetItem: ImageStorageSetItemUseCase,
    private val groupSetItem: GroupSetItemUseCase,
    private val setChatItem: ChatSetItemUseCase,
    private val updateGroupInfo: UserUpdateGroupInfoUseCase,
    private val groupSetMemberItem: GroupSetMemberItemUseCase,
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

    private val _isLoadingState: MutableLiveData<Boolean> = MutableLiveData()
    val isLoadingState: LiveData<Boolean> get() = _isLoadingState

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

    /**
     * 모임 생성 및 채팅방 생성
     */
    fun setGroupAddItem() {
        if (isCorrectGroupAddItem()) {
            _isLoadingState.value = true
            viewModelScope.launch {
                runCatching {
                    getImageStorage("imgMainImage", Uri.parse(groupAddMain.value?.mainImage))
                    getImageStorage(
                        "imgBackgroundImage",
                        Uri.parse(groupAddMain.value?.backgroundImage)
                    )

                    val userInfo = getUserInfo()
                    // 모임 생성
                    val groupId = groupSetItem(combineGroupItem())
                    groupSetMemberItem(
                        groupId,
                        GroupDetailMemberAddItem(
                            userId = userInfo?.userId ?: "UserId",
                            profile = userInfo?.userProfile,
                            name = userInfo?.userName ?: "UserName",
                            location = userInfo?.userLocation ?: "UserLocation",
                            comment = "(방장)"
                        )
                    )

                    // 채팅방 생성
                    val chatId = setChatItem(
                        GroupDetailChatItem(
                            groupId = groupId,
                            mainImage = groupAddMain.value?.mainImage,
                            backgroundImage = groupAddMain.value?.backgroundImage,
                            title = groupAddMain.value?.groupName ?: "Group Name"
                        )
                    )

                    //사용자 정보 업데이트 (방장 자격)
                    updateGroupInfo(getUserInfo()?.userId ?: "UserId", groupId, chatId)

                    _isLoadingState.value = false
                    _isCreateSuccess.value = true
                }.onFailure {
                    _isLoadingState.value = false
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
            add(GroupAddSetItem.GroupAddMember())
            add(GroupAddSetItem.GroupAddBoard())
            add(GroupAddSetItem.GroupAddAlbum())
        }

    private suspend fun getImageStorage(currentItem: String, image: Uri) {
        runCatching {
            imageStorageSetItem(image).collect { imageUri ->
                when (currentItem) {
                    "imgMainImage" -> groupAddMain.value =
                        groupAddMain.value?.copy(mainImage = imageUri.toString())

                    "imgBackgroundImage" -> groupAddMain.value =
                        groupAddMain.value?.copy(backgroundImage = imageUri.toString())
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
    private val databaseReference = FirebaseDatabase.getInstance()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val userPrefRepository = UserPreferencesRepositoryImpl(
            null,
            UserInfoPreferenceImpl(
                context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
            )
        )
        val imageStorageRepository =
            ImageStorageRepositoryImpl(FirebaseStorage.getInstance().reference)
        val groupRepository = GroupRepositoryImpl(databaseReference.getReference("group_list"))
        val chatSetRepository =
            ChatRepositoryImpl(databaseReference.getReference("chat_list").child("group"), null)
        val userUpdateRepository =
            UserRepositoryImpl(databaseReference.getReference("users"), null, null)
        if (modelClass.isAssignableFrom(GroupAddSharedViewModel::class.java)) {
            return GroupAddSharedViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                ImageStorageSetItemUseCase(imageStorageRepository),
                GroupSetItemUseCase(groupRepository),
                ChatSetItemUseCase(chatSetRepository),
                UserUpdateGroupInfoUseCase(userUpdateRepository),
                GroupSetMemberItemUseCase(groupRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}