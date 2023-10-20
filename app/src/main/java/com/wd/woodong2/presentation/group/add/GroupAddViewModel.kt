package com.wd.woodong2.presentation.group.add

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.domain.usecase.GroupSetItemUseCase
import kotlinx.coroutines.launch

class GroupAddViewModel(
    private val groupSetItem: GroupSetItemUseCase
) : ViewModel() {
    private val _groupAddList: MutableLiveData<List<GroupAddGetItem>> = MutableLiveData()
    val groupAddList: LiveData<List<GroupAddGetItem>> get() = _groupAddList

    fun initGroupAddItems(groupAddGetItems: List<GroupAddGetItem>) {
        _groupAddList.value = groupAddGetItems
    }

    fun updatePasswordChecked(position: Int, item: GroupAddGetItem.Password) {
        if (position < 0) {
            return
        }
        val currentList = _groupAddList.value.orEmpty().toMutableList()
        currentList[position] = item
        _groupAddList.value = currentList
    }

    fun updateImage(position: Int, item: GroupAddGetItem.Image, uri: Uri) {
        val currentList = _groupAddList.value.orEmpty().toMutableList()
        currentList[position] = item.copy(image = uri)
        _groupAddList.value = currentList
    }

    fun setGroupAddItem(groupAddSetItem: GroupAddSetItem) = viewModelScope.launch {
        runCatching {
            groupSetItem(groupAddSetItem)
        }.onFailure {
            Log.e("sinw", it.message.toString())
        }
    }
}

class GroupAddViewModelFactory : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        val repository = GroupRepositoryImpl(FirebaseDatabase.getInstance().getReference("group_list"))
        if(modelClass.isAssignableFrom(GroupAddViewModel::class.java)) {
            return GroupAddViewModel(
                GroupSetItemUseCase(repository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}