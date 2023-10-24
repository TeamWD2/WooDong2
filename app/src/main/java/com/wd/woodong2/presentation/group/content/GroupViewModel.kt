package com.wd.woodong2.presentation.group.content

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.domain.model.GroupAlbumEntity
import com.wd.woodong2.domain.model.GroupBoardEntity
import com.wd.woodong2.domain.model.GroupIntroduceEntity
import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.model.GroupMemberEntity
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
                Log.d("sinw", "items / $items")
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
        return items.groupList.map { entity ->
            when(entity) {
                is GroupIntroduceEntity -> GroupItem.GroupIntroduce(
                    title = entity.title,
                    groupTag = entity.groupTag,
                    groupName = entity.groupName,
                    introduce = entity.introduce,
                    ageLimit = entity.ageLimit,
                    memberLimit = entity.memberLimit,
                    password = entity.password,
                    mainImage = entity.mainImage,
                    backgroundImage = entity.backgroundImage,
                    timestamp = entity.timestamp
                )
                is GroupMemberEntity -> GroupItem.GroupMember(
                    title = entity.title,
                    memberList = entity.memberList?.map { member ->
                        GroupItem.Member(
                            userId = member.userId,
                            profile = member.profile,
                            name = member.name,
                            location = member.location
                        )
                    }
                )
                is GroupBoardEntity -> GroupItem.GroupBoard(
                    title = entity.title,
                    boardList = entity.boardList?.map { board ->
                        GroupItem.Board(
                            userId = board.userId,
                            profile = board.profile,
                            name = board.name,
                            location = board.location,
                            timestamp = board.timestamp,
                            content = board.content,
                            images = board.images
                        )
                    }
                )
                is GroupAlbumEntity -> GroupItem.GroupAlbum(
                    title = entity.title,
                    images = entity.images
                )
            }
        }
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