package com.wd.woodong2.presentation.group.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wd.woodong2.presentation.group.content.GroupItem

class GroupDetailSharedViewModel: ViewModel() {
    private val _tabName: MutableLiveData<Int> = MutableLiveData()
    val tabName: LiveData<Int> get() = _tabName

    var groupDetailItem: List<GroupItem>? = null

    fun setDetailItem(groupList: List<GroupItem>?) {
        groupDetailItem = groupList
    }

    fun modifyTab(tabName: Int) {
        _tabName.value = tabName
    }
}