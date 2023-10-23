package com.wd.woodong2.presentation.group.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wd.woodong2.presentation.group.content.GroupItem

class GroupDetailSharedViewModel: ViewModel() {
    private val _groupDetailItem: MutableLiveData<GroupItem?> = MutableLiveData()
    val groupDetailItem: LiveData<GroupItem?> get() = _groupDetailItem

    fun setDetailItem(groupItem: GroupItem?) {
        _groupDetailItem.value = groupItem
    }
}