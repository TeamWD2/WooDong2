package com.wd.woodong2.presentation.group.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.domain.usecase.GroupSetItemUseCase
import kotlinx.coroutines.launch

class GroupAddSharedViewModel(
    private val groupSetItem: GroupSetItemUseCase
) : ViewModel() {

    fun setGroupAddItem(
        groupAddSetItem: List<GroupAddSetItem>
    ) = viewModelScope.launch {
        runCatching {
            groupSetItem(groupAddSetItem)
        }.onFailure {
            Log.e("sinw", it.message.toString())
        }
    }
}

class GroupAddSharedViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository =
            GroupRepositoryImpl(FirebaseDatabase.getInstance().getReference("group_list"))
        if (modelClass.isAssignableFrom(GroupAddSharedViewModel::class.java)) {
            return GroupAddSharedViewModel(
                GroupSetItemUseCase(repository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}