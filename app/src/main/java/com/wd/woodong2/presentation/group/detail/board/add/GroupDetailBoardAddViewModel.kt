package com.wd.woodong2.presentation.group.detail.board.add

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
import com.wd.woodong2.domain.usecase.GroupSetAlbumItemUseCase
import com.wd.woodong2.domain.usecase.GroupSetBoardItemUseCase
import com.wd.woodong2.domain.usecase.ImageStorageSetItemUseCase
import com.wd.woodong2.presentation.group.GroupUserInfoItem
import kotlinx.coroutines.launch
import java.util.UUID

class GroupDetailBoardAddViewModel(
    private val imageStorageSetItem: ImageStorageSetItemUseCase,
    private val groupSetBoardItem: GroupSetBoardItemUseCase,
    private val groupSetAlbumItem: GroupSetAlbumItemUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "GroupDetailBoardAddViewModel"
    }

    private val _imageList: MutableLiveData<List<GroupDetailBoardAddImageItem>> = MutableLiveData()
    val imageList: LiveData<List<GroupDetailBoardAddImageItem>> get() = _imageList

    private val _isCreateSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isCreateSuccess: LiveData<Boolean> get() = _isCreateSuccess

    fun addBoardImageItem(item: GroupDetailBoardAddImageItem?) {
        if (item == null) {
            return
        }
        val currentList = imageList.value.orEmpty().toMutableList()
        _imageList.value = currentList.apply {
            add(item)
        }
    }

    fun updateBoardImageItem(item: GroupDetailBoardAddImageItem?) {
        fun findIndex(item: GroupDetailBoardAddImageItem?): Int {
            val currentList = imageList.value.orEmpty().toMutableList()
            val findTodo = currentList.find {
                it.id == item?.id
            }
            return currentList.indexOf(findTodo)
        }

        if (item == null) {
            return
        }

        val findPosition = findIndex(item)
        if (findPosition < 0) {
            return
        }

        val currentList = imageList.value.orEmpty().toMutableList()
        currentList[findPosition] = item
        _imageList.value = currentList
    }

    fun removeBoardImageItem(position: Int?) {
        if (position == null || position < 0) {
            return
        }

        val currentList = imageList.value.orEmpty().toMutableList()
        currentList.removeAt(position)
        _imageList.value = currentList
    }

    fun setGroupBoardAlbumItem(
        itemId: String?,
        userInfo: GroupUserInfoItem?,
        edtContent: String
    ) {
        if (itemId == null || userInfo == null) {
            return
        }
        viewModelScope.launch {
            runCatching {
                val uriImageList = createBoardImages()
                groupSetBoardItem(
                    itemId,
                    GroupDetailBoardAddItem(
                        userId = userInfo.userId,
                        profile = userInfo.userProfile,
                        name = userInfo.userName,
                        location = userInfo.userLocation,
                        content = edtContent,
                        images = uriImageList
                    )
                )
                groupSetAlbumItem(
                    itemId,
                    uriImageList
                )
                _isCreateSuccess.value = true
            }.onFailure {
                Log.e(TAG, it.message.toString())
                _isCreateSuccess.value = false
            }
        }
    }

    private suspend fun createBoardImages(): List<String> {
        val uriToStringList = mutableListOf<String>()
        _imageList.value?.forEach { item ->
            item.uri?.let { uri ->
                runCatching {
                    imageStorageSetItem(uri).collect { imageUri ->
                        uriToStringList.add(imageUri.toString())
                    }
                }
            }
        }
        return uriToStringList
    }
}

class GroupDetailBoardAddViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val imageStorageRepository =
            ImageStorageRepositoryImpl(FirebaseStorage.getInstance().reference)
        val groupSetItemRepository =
            GroupRepositoryImpl(FirebaseDatabase.getInstance().getReference("group_list"))
        if (modelClass.isAssignableFrom(GroupDetailBoardAddViewModel::class.java)) {
            return GroupDetailBoardAddViewModel(
                ImageStorageSetItemUseCase(imageStorageRepository),
                GroupSetBoardItemUseCase(groupSetItemRepository),
                GroupSetAlbumItemUseCase(groupSetItemRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}