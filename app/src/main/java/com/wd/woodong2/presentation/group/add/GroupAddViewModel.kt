package com.wd.woodong2.presentation.group.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GroupAddViewModel : ViewModel() {
    private val _groupAddList: MutableLiveData<List<GroupAddGetItem>> = MutableLiveData()
    val groupAddList: LiveData<List<GroupAddGetItem>> get() = _groupAddList

    fun initGroupAddItem(groupAddGetItems: List<GroupAddGetItem>) {
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
}