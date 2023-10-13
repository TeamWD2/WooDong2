package com.wd.woodong2.presentation.group.content

import android.util.Log
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
): ViewModel() {
    private val _groupList: MutableLiveData<List<GroupItem>> = MutableLiveData()
    val groupList: LiveData<List<GroupItem>> get() = _groupList

    fun getGroupItem() = viewModelScope.launch {
        runCatching {
            groupItem { items ->
                val groupItemList = items?.groupItems?.map {
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
            }
        }.onFailure {
            Log.e("sinw", it.message.toString())
        }
    }
//
//    fun addGroupItem() = viewModelScope.launch {
//        val database = FirebaseDatabase.getInstance()
//        val databaseReference = database.getReference("items")
//
//        runCatching {
//            val newItem = GroupItem(
//                imgGroupProfile = "https://i.ytimg.com/vi/dhZH7NLCOmk/default.jpg",
//                txtTitle="Test Title 1111",
//                imgMemberProfile1 = "https://i.ytimg.com/vi/dhZH7NLCOmk/default.jpg",
//                imgMemberProfile2 = "https://i.ytimg.com/vi/dhZH7NLCOmk/default.jpg",
//                imgMemberProfile3 = "https://i.ytimg.com/vi/dhZH7NLCOmk/default.jpg",
//                txtMemberCount = 5,
//                txtTagLocation = "권선동",
//                txtTagCategory = "동네모임",
//                txtTagCapacity = 50
//            )
//
//            val newItemReference = databaseReference.push()
//            newItemReference.setValue(newItem) { databaseError, _ ->
//                if(databaseError != null) {
//                    Log.e("sinw", "실패 ${databaseError.message}")
//                } else {
//                    Log.e("sinw", "성공!!")
//                }
//            }
//        }
//    }
}

class GroupViewModelFactory(
    private val repository: GroupRepositoryImpl
): ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            return GroupViewModel(
                GroupGetItemsUseCase(repository)
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}