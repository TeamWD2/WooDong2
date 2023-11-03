package com.wd.woodong2.presentation.group.detail.board.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.wd.woodong2.data.repository.ImageStorageRepositoryImpl
import com.wd.woodong2.domain.usecase.ImageStorageSetItemUseCase
import kotlinx.coroutines.launch
import java.util.UUID

class GroupDetailBoardAddViewModel(
    private val imageStorageSetItem: ImageStorageSetItemUseCase
) : ViewModel() {
    companion object {
        private const val TAG = "GroupDetailBoardAddViewModel"
    }


    private val _imageList: MutableLiveData<List<GroupDetailBoardAddImageItem>> = MutableLiveData()
    val imageList: LiveData<List<GroupDetailBoardAddImageItem>> get() = _imageList


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
        if(position == null || position < 0) {
            return
        }

        val currentList = imageList.value.orEmpty().toMutableList()
        currentList.removeAt(position)
        _imageList.value = currentList
    }
}

class GroupDetailBoardAddViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val imageStorageRepository =
            ImageStorageRepositoryImpl(FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}"))
        if (modelClass.isAssignableFrom(GroupDetailBoardAddViewModel::class.java)) {
            return GroupDetailBoardAddViewModel(
                ImageStorageSetItemUseCase(imageStorageRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}