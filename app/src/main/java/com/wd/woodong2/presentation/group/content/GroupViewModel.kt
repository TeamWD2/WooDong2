package com.wd.woodong2.presentation.group.content

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.usecase.GroupGetItemsUseCase
import kotlinx.coroutines.launch

class GroupViewModel(
    private val groupGetItems: GroupGetItemsUseCase
) : ViewModel() {
    private val _groupList: MutableLiveData<List<GroupItem>> = MutableLiveData()
    val groupList: LiveData<List<GroupItem>> get() = _groupList

    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _isEmptyList: MutableLiveData<Boolean> = MutableLiveData()
    val isEmptyList: LiveData<Boolean> get() = _isEmptyList

    fun getGroupItem() = viewModelScope.launch {
        _loadingState.value = true
        runCatching {
            groupGetItems().collect { items ->
                _isEmptyList.value = readGroupItems(items).isEmpty()
                _groupList.postValue(readGroupItems(items))
                _loadingState.value = false
            }
        }.onFailure {
            Log.e("sinw", it.message.toString())
            _loadingState.value = false
        }
    }

    /**
     * Firebase 에서 모임 목록 read
     */
    private fun readGroupItems(
        items: GroupItemsEntity
    ) = items.groupItems?.map {
        GroupItem(
            id = it.id,
            title = it.title,
            introduce = it.introduce,
            groupTag = it.groupTag,
            ageLimit = it.ageLimit,
            memberLimit = it.memberLimit,
            memberList = it.memberList?.map { member ->
                Member(
                    userId = member.userId ?: "",
                    userName = member.userName ?: "",
                    userProfile = member.userProfile ?: ""
                )
            },
            password = it.password,
            mainImage = it.mainImage,
            backgroundImage = it.backgroundImage,
        )
    }.orEmpty()
}

class GroupViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val databaseReference = FirebaseDatabase.getInstance().getReference("group_list")
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