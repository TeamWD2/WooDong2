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
                val groupItems = readGroupItems(items)
                _isEmptyList.value = groupItems.isEmpty()
                _groupList.postValue(groupItems)
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
    ): List<GroupItem> {
        val groupList = items.groupItems.map {
            GroupItem(
                id = it.id,
                introduce = GroupIntroduce(
                    title = it.introduce?.title,
                    introduce = it.introduce?.introduce,
                    groupTag = it.introduce?.groupTag,
                    ageLimit = it.introduce?.ageLimit,
                    memberLimit = it.introduce?.memberLimit,
                    password = it.introduce?.password,
                    mainImage = it.introduce?.mainImage,
                    backgroundImage = it.introduce?.backgroundImage,
                    timestamp = it.introduce?.timestamp,
                ),
                member = GroupMember(
                    memberList = it.member?.memberList?.map { member ->
                        Member(
                            userId = member.userId,
                            userName = member.userName,
                            userProfile = member.userProfile
                        )
                    }
                ),
                board = GroupBoard(
                    boardList = it.board?.boardList?.map { board ->
                        Board(
                            userId = board.userId,
                            userName = board.userName,
                            userProfile = board.userProfile,
                            timestamp = board.timestamp,
                            content = board.content,
                            photoList = board.photoList?.map { photo ->
                                Photo(
                                    photo = photo.photo
                                )
                            }
                        )
                    }
                )
            )
        }
        return groupList
    }
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