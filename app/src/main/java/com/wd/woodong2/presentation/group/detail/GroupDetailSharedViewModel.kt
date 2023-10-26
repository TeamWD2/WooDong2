package com.wd.woodong2.presentation.group.detail

import androidx.lifecycle.ViewModel
import com.wd.woodong2.presentation.group.content.GroupItem

class GroupDetailSharedViewModel: ViewModel() {
    var groupDetailItem: List<GroupItem>? = null

    fun setDetailItem(groupList: List<GroupItem>?) {
        groupDetailItem = groupList
    }
}