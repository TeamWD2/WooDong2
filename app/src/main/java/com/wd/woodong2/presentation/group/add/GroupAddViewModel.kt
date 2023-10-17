package com.wd.woodong2.presentation.group.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GroupAddViewModel : ViewModel() {
    private val _groupAddList: MutableLiveData<List<GroupAddItem>> = MutableLiveData()
    val groupAddList: LiveData<List<GroupAddItem>> get() = _groupAddList

    fun initGroupAddItem(groupAddItems: List<GroupAddItem>) {
        _groupAddList.value = groupAddItems
    }

    fun updateGroupAddItem(position: Int, item: GroupAddItem.Password) {
        if(position < 0) {
            return
        }
        val currentList = _groupAddList.value.orEmpty().toMutableList()
        currentList[position] = item
        _groupAddList.value = currentList
    }
}