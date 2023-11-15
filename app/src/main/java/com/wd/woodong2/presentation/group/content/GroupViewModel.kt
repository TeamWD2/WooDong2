package com.wd.woodong2.presentation.group.content

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.wd.woodong2.R
import com.wd.woodong2.data.repository.GroupRepositoryImpl
import com.wd.woodong2.data.repository.MapSearchRepositoryImpl
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.model.GroupAlbumEntity
import com.wd.woodong2.domain.model.GroupBoardEntity
import com.wd.woodong2.domain.model.GroupIntroduceEntity
import com.wd.woodong2.domain.model.GroupItemsEntity
import com.wd.woodong2.domain.model.GroupMainEntity
import com.wd.woodong2.domain.model.GroupMemberEntity
import com.wd.woodong2.domain.model.MapSearchEntity
import com.wd.woodong2.domain.model.UserEntity
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.repository.MapSearchRepository
import com.wd.woodong2.domain.usecase.GroupGetItemsUseCase
import com.wd.woodong2.domain.usecase.MapSearchCircumLocationGetItemsUseCase
import com.wd.woodong2.domain.usecase.MapSearchGetItemsUseCase
import com.wd.woodong2.domain.usecase.UserPrefEditItemUseCase
import com.wd.woodong2.domain.usecase.UserPrefGetItemUseCase
import com.wd.woodong2.domain.usecase.UserUpdateInfoUseCase
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.group.GroupUserInfoItem
import com.wd.woodong2.presentation.home.map.HomeMapActivity
import com.wd.woodong2.presentation.home.map.HomeMapSearchItem
import com.wd.woodong2.retrofit.KAKAORetrofitClient
import kotlinx.coroutines.launch

class GroupViewModel(
    private val groupGetItems: GroupGetItemsUseCase,
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val userUpdateInfoUseCase: UserUpdateInfoUseCase,
    private val circumLocationItem: MapSearchCircumLocationGetItemsUseCase,
    private val userPrefEditItemUseCase: UserPrefEditItemUseCase,
    private val mapSearch: MapSearchGetItemsUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "GroupViewModel"
    }

    private val _groupList: MutableLiveData<List<GroupItem>> = MutableLiveData()
    val groupList: LiveData<List<GroupItem>> get() = _groupList

    private val _searchKeyword: MutableLiveData<String> = MutableLiveData()
    val searchKeyword: LiveData<String> get() = _searchKeyword

    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _isEmptyList: MutableLiveData<Boolean> = MutableLiveData()
    val isEmptyList: LiveData<Boolean> get() = _isEmptyList

    var userInfo: MutableLiveData<UserItem?> = MutableLiveData()

    val _printList: MutableLiveData<List<GroupItem>?> = MutableLiveData()
    val printList: LiveData<List<GroupItem>?> get() = _printList

    private val _circumLocationList: MutableLiveData<List<HomeMapSearchItem>> = MutableLiveData()
    val circumLocationList: LiveData<List<HomeMapSearchItem>> get() = _circumLocationList

    var circumLocation = mutableSetOf<String>()

//    init {
//        userInfo.postValue(getUserInfo())
//    }
    fun setKeyword(keyword: String) {
        _searchKeyword.value = keyword
    }

    fun searchKeywordGroupItem(keyword: String): List<GroupItem.GroupMain> {
        val groupMainItem = _printList.value?.filterIsInstance<GroupItem.GroupMain>()
        return if (keyword.isBlank()) {
            groupMainItem
        } else {
            groupMainItem?.filter {
                it.groupName?.contains(keyword) == true || it.introduce?.contains(keyword) == true
            }
        } ?: listOf()
    }

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
            Log.e(TAG, it.message.toString())
            _loadingState.value = false
        }
    }

    fun getUserInfo() =
        prefGetUserItem()?.let {
            UserItem(
                id = it.id ?: "unknown",
                name = it.name ?: "unknown",
                imgProfile = it.imgProfile,
                email = it.email ?: "unknown",
                chatIds = it.chatIds,
                groupIds = it.groupIds,
                likedIds = it.likedIds,
                writtenIds = it.writtenIds,
                firstLocation = it.firstLocation ?: "unknown",
                secondLocation = it.secondLocation ?: "unknown"
            )
        }

    fun updateUserLocation(
        firstLocation: String,
        secondLocation: String,
    ) = viewModelScope.launch {
        runCatching {
            userUpdateInfoUseCase(
                getUserInfo()?.id ?: "(알 수 없음)",
                getUserInfo()?.imgProfile.toString(),
                getUserInfo()?.name.toString(),
                firstLocation,
                secondLocation
            )
        }.onFailure {
            Log.e(TAG, it.message.toString())
        }
    }

    fun editPrefUserInfo(
        name: String?,
        imgProfile: String?,
        firstLocation: String?,
        secondLocation: String?
    ) = userPrefEditItemUseCase(name, imgProfile, firstLocation, secondLocation)?.let {
        UserItem(
            id = it.id ?: "unknown",
            name = it.name ?: "unknown",
            imgProfile = it.imgProfile,
            email = it.email ?: "unknown",
            chatIds = it.chatIds,
            groupIds = it.groupIds,
            likedIds = it.likedIds,
            writtenIds = it.writtenIds,
            firstLocation = it.firstLocation ?: "unknown",
            secondLocation = it.secondLocation ?: "unknown"
        )
    }

    /**
     * Firebase 에서 모임 목록 read
     */
    private fun readGroupItems(
        items: GroupItemsEntity
    ): List<GroupItem> {
        return items.groupList.map { entity ->
            when (entity) {
                is GroupMainEntity -> GroupItem.GroupMain(
                    id = entity.id,
                    title = "Main",
                    groupName = entity.groupName,
                    introduce = entity.introduce,
                    groupTag = entity.groupTag,
                    ageLimit = entity.ageLimit,
                    memberLimit = entity.memberLimit,
                    password = entity.password,
                    mainImage = entity.mainImage,
                    backgroundImage = entity.backgroundImage,
                    groupLocation = entity.groupLocation
                )

                is GroupIntroduceEntity -> GroupItem.GroupIntroduce(
                    id = entity.id,
                    title = entity.title,
                    introduce = entity.introduce,
                    groupTag = entity.groupTag,
                    ageLimit = entity.ageLimit,
                    memberLimit = entity.memberLimit,
                )

                is GroupMemberEntity -> GroupItem.GroupMember(
                    id = entity.id,
                    title = entity.title,
                    memberList = entity.memberList?.map { member ->
                        GroupItem.Member(
                            userId = member.userId,
                            profile = member.profile,
                            name = member.name,
                            location = member.location,
                            comment = member.comment
                        )
                    }
                )

                is GroupBoardEntity -> GroupItem.GroupBoard(
                    id = entity.id,
                    title = entity.title,
                    boardList = entity.boardList?.toSortedMap(reverseOrder())
                        ?.mapValues { (boardId, board) ->
                            GroupItem.Board(
                                boardId = boardId,
                                userId = board.userId,
                                profile = board.profile,
                                name = board.name,
                                location = board.location,
                                timestamp = board.timestamp,
                                content = board.content,
                                images = board.images,
                                commentList = board.commentList?.toSortedMap()
                                    ?.mapValues { (commentId, comment) ->
                                        GroupItem.BoardComment(
                                            commentId = commentId,
                                            userId = comment.userId,
                                            userProfile = comment.userProfile,
                                            userName = comment.userName,
                                            userLocation = comment.userLocation,
                                            timestamp = comment.timestamp,
                                            comment = comment.comment
                                        )
                                    }?.values?.toList()
                            )
                        }?.values?.toList()
                )

                is GroupAlbumEntity -> GroupItem.GroupAlbum(
                    id = entity.id,
                    title = entity.title,
                    images = entity.images?.toSortedMap(reverseOrder())?.values?.toList()
                )
            }
        }.sortedByDescending { it.id }
    }

    fun circumLocationItemSearch(
        y: Double,
        x: Double,
        radius: Int,
        query: String,
        userLocation: String,
    ) = viewModelScope.launch {
        runCatching {
            val set = "주민센터"
            if (circumLocation.isNotEmpty()) {
                circumLocation.clear()
            }

            var circumLocationItems = createCircumLocationItems(
                Map = circumLocationItem(
                    y,
                    x,
                    radius,
                    "$query $set"
                )
            )

            HomeMapActivity.fullNameLocationInfo(query)
            circumLocation.add(HomeMapActivity.fullLocationName.toString())
            _circumLocationList.postValue(circumLocationItems)

            for (item in circumLocationItems) {
                if (item is HomeMapSearchItem.MapSearchItem) {
                    val address = item.address
                    if (address != null) {
                        HomeMapActivity.fullNameLocationInfo(address)
                        if (HomeMapActivity.extractLocationInfo(userLocation) != HomeMapActivity.extractLocationInfo(
                                address
                            )
                        )  //사용자 현재위치는 설정 x
                        {
                            circumLocation.add(HomeMapActivity.fullLocationName.toString())
                        }
                    }
                }
            }

            circumLocationItems = createCircumLocationItems(
                Map = mapSearch(query)
            )
            _circumLocationList.postValue(circumLocationItems)


            //주변 위치 설정하기
            for (item in circumLocationItems) {
                if (item is HomeMapSearchItem.MapSearchItem) {
                    val address = item.address
                    if (address != null) {
                        HomeMapActivity.fullNameLocationInfo(address)
                        if (HomeMapActivity.extractLocationInfo(userLocation) != HomeMapActivity.extractLocationInfo(
                                address
                            )
                        )  //사용자 현재위치는 설정 x
                        {
                            circumLocation.add(HomeMapActivity.fullLocationName.toString())
                        }
                    }
                }
            }
            if (circumLocation.isNotEmpty()) {

                _printList.value = groupList.value?.filter { item ->
                    item is GroupItem.GroupMain && circumLocation.contains(item.groupLocation)
                }

            }

            if ((printList.value?.size ?: 0) < 5) {
                val circumLocationItems = createCircumLocationItems(
                    Map = circumLocationItem(
                        y,
                        x,
                        radius,
                        "${HomeMapActivity.extractDistrictInfo(query)} $set"
                    )
                )
                _circumLocationList.postValue(circumLocationItems)
                //주변 위치 설정하기
                for (item in circumLocationItems) {
                    if (item is HomeMapSearchItem.MapSearchItem) {
                        val address = item.address
                        if (address != null) {
                            HomeMapActivity.fullNameLocationInfo(address)
                            if (HomeMapActivity.extractLocationInfo(userLocation.toString()) != HomeMapActivity.extractLocationInfo(
                                    address
                                )
                            )  //사용자 현재위치는 설정 x
                            {
                                circumLocation.add(HomeMapActivity.fullLocationName.toString())
                            }
                        }
                    }
                }
                if (circumLocation.isNotEmpty()) {
                    _printList.value = groupList.value?.filter { item ->
                        item is GroupItem.GroupMain && circumLocation.contains(item.groupLocation)
                    }
                }
                if ((printList.value?.size ?: 0) < 5) {
                    val circumLocationItems = createCircumLocationItems(
                        Map = circumLocationItem(
                            y,
                            x,
                            radius,
                            "${HomeMapActivity.extractCityInfo(query)} $set"
                        )
                    )
                    _circumLocationList.postValue(circumLocationItems)

                    //주변 위치 설정하기
                    for (item in circumLocationItems) {
                        if (item is HomeMapSearchItem.MapSearchItem) {
                            val address = item.address
                            if (address != null) {
                                HomeMapActivity.fullNameLocationInfo(address)
                                if (HomeMapActivity.extractLocationInfo(userLocation) != HomeMapActivity.extractLocationInfo(
                                        address
                                    )
                                ) {
                                    circumLocation.add(HomeMapActivity.fullLocationName.toString())
                                }
                            }
                        }
                    }
                    if (circumLocation.isNotEmpty()) {
                        for (loc in circumLocation) {
                            _printList.value = groupList.value?.filter { item ->
                                item is GroupItem.GroupMain && circumLocation.contains(item.groupLocation)
                            }
                        }
                        if ((printList.value?.size ?: 0) < 5) {
                            val existingList = _printList.value.orEmpty()
                            val filteredList = groupList.value?.filter { item ->
                                item is GroupItem.GroupMain && !circumLocation.contains(item.groupLocation)
                            }

                            val combinedList = existingList.toMutableList()
                                .apply { addAll(filteredList.orEmpty()) }
                            _printList.value = combinedList

                        }
                    }
                }
            }
        }.onFailure { e ->
            Log.e("Retrofit Error", "Request failed: ${e.message}")
        }
    }

    private fun createCircumLocationItems(
        Map: MapSearchEntity,
    ): List<HomeMapSearchItem> {
        fun createMapSearchItems(
            Map: MapSearchEntity,
        ): List<HomeMapSearchItem.MapSearchItem> = Map.documents.map { document ->
            HomeMapSearchItem.MapSearchItem(
                address = document.addressName,
                x = document.x,
                y = document.y
            )
        }
        return createMapSearchItems(Map)
    }
}

class GroupViewModelFactory(
    val context: Context
) : ViewModelProvider.Factory {
    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val userPrefRepository = UserPreferencesRepositoryImpl(
            null,
            UserInfoPreferenceImpl(
                context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
            )
        )
        val userRepositoryImpl by lazy {
            UserRepositoryImpl(
                FirebaseDatabase.getInstance().getReference("user_list"),
                Firebase.auth,
                FirebaseTokenProvider(FirebaseMessaging.getInstance())
            )
        }
        val circumLocationrepository: MapSearchRepository = MapSearchRepositoryImpl(
            KAKAORetrofitClient.search
        )
        val databaseReference = FirebaseDatabase.getInstance().getReference("group_list")
        val groupGetRepository = GroupRepositoryImpl(databaseReference)
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            return GroupViewModel(
                GroupGetItemsUseCase(groupGetRepository),
                UserPrefGetItemUseCase(userPrefRepository),
                UserUpdateInfoUseCase(userRepositoryImpl),
                MapSearchCircumLocationGetItemsUseCase(circumLocationrepository),
                UserPrefEditItemUseCase(userPrefRepository),
                MapSearchGetItemsUseCase(circumLocationrepository),
            ) as T
        } else {
            throw IllegalArgumentException("Not Found ViewModel Class")
        }
    }
}