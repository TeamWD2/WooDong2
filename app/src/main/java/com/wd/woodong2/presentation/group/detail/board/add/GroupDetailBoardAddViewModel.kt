package com.wd.woodong2.presentation.group.detail.board.add

import android.net.Uri
import android.util.Log
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

    private val _galleryStorage1: MutableLiveData<Uri> = MutableLiveData()
    val galleryStorage1: LiveData<Uri> get() = _galleryStorage1

    private val _galleryStorage2: MutableLiveData<Uri> = MutableLiveData()
    val galleryStorage2: LiveData<Uri> get() = _galleryStorage2

    private val _galleryStorage3: MutableLiveData<Uri> = MutableLiveData()
    val galleryStorage3: LiveData<Uri> get() = _galleryStorage3

    fun getGalleryImage(
        clickedImage: String?,
        image: Uri
    ) = viewModelScope.launch {
        runCatching {
            imageStorageSetItem(image).collect { imageUri ->
                when (clickedImage) {
                    "imgPhoto1" -> _galleryStorage1.value = imageUri
                    "imgPhoto2" -> _galleryStorage2.value = imageUri
                    "imgPhoto3" -> _galleryStorage3.value = imageUri
                }
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
        }
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