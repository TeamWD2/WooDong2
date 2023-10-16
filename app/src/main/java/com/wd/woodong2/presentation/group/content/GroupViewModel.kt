package com.wd.woodong2.presentation.group.content

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.domain.usecase.GroupGetItemsUseCase
import kotlinx.coroutines.launch

class GroupViewModel(
    private val groupItem: GroupGetItemsUseCase
) : ViewModel() {
    private val _groupList: MutableLiveData<List<GroupItem>> = MutableLiveData()
    val groupList: LiveData<List<GroupItem>> get() = _groupList

    fun getGroupItem() = viewModelScope.launch {
        runCatching {
            val items = groupItem()
            val groupItemList = items.groupItems?.map {
                GroupItem(
                    imgGroupProfile = it.groupProfile,
                    txtTitle = it.title,
                    imgMemberProfile1 = it.memberProfile1,
                    imgMemberProfile2 = it.memberProfile2,
                    imgMemberProfile3 = it.memberProfile3,
                    txtMemberCount = it.memberCount,
                    txtTagLocation = it.tagLocation,
                    txtTagCategory = it.tagCategory,
                    txtTagCapacity = it.tagCapacity
                )
            }.orEmpty()
            _groupList.postValue(groupItemList)
        }.onFailure {
            Log.e("sinw", it.message.toString())
        }
    }
}

class GroupViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val databaseReference = FirebaseDatabase.getInstance().getReference("items")
        val repository = GroupRepositoryImpl(databaseReference)

        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            return GroupViewModel(
                GroupGetItemsUseCase(repository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}