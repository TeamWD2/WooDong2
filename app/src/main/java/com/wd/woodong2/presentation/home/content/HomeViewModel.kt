package com.wd.woodong2.presentation.home.content

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.presentation.chat.content.UserItem
import kotlinx.coroutines.launch
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.wd.woodong2.R
import com.wd.woodong2.data.repository.MapSearchRepositoryImpl
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.data.sharedpreference.SignInPreferenceImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.model.MapSearchEntity
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.repository.MapSearchRepository
import com.wd.woodong2.domain.usecase.map.MapSearchCircumLocationGetItemsUseCase
import com.wd.woodong2.domain.usecase.map.MapSearchGetItemsUseCase
import com.wd.woodong2.domain.usecase.user.UserGetItemUseCase
import com.wd.woodong2.domain.usecase.prefs.UserPrefEditItemUseCase
import com.wd.woodong2.domain.usecase.prefs.UserPrefGetItemUseCase
import com.wd.woodong2.domain.usecase.user.UserRemoveIdsUseCase
import com.wd.woodong2.domain.usecase.user.UserUpdateInfoUseCase
import com.wd.woodong2.presentation.home.map.HomeMapActivity
import com.wd.woodong2.presentation.home.map.HomeMapSearchItem
import com.wd.woodong2.presentation.home.map.HomeMapSearchViewModel
import com.wd.woodong2.retrofit.KAKAORetrofitClient

class HomeViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val userItem: UserGetItemUseCase,
    private val userUpdateInfoUseCase: UserUpdateInfoUseCase,
    private val circumLocationItem: MapSearchCircumLocationGetItemsUseCase,
    private val mapSearch: MapSearchGetItemsUseCase,
    private val userRemoveIdsUseCase: UserRemoveIdsUseCase,
    private val userPrefEditItemUseCase: UserPrefEditItemUseCase,
) : ViewModel(
) {
    companion object {
        private val TAG = "HomeViewModel"
    }

    // 필터링된 아이템을 저장하는 LiveData
    private val _filteredItems = MutableLiveData<List<HomeItem>>()

    val filteredItems: LiveData<List<HomeItem>> = _filteredItems

    private val _list: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val list: LiveData<List<HomeItem>> get() = _list


    private val _circumLocationList: MutableLiveData<List<HomeMapSearchItem>> = MutableLiveData()
    val circumLocationList: LiveData<List<HomeMapSearchItem>> get() = _circumLocationList

    private val _searchKeyword: MutableLiveData<String> = MutableLiveData()
    val searchKeyword: LiveData<String> get() = _searchKeyword

    private val _deleteResults = MutableLiveData<List<HomeItem>>()

    val deleteResults: LiveData<List<HomeItem>> = _deleteResults

    //주변 위치 값 받아오기
    var circumLocation = mutableSetOf<String>()

    //Home에 출력할 list 설정하기
    val _printList: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val printList: LiveData<List<HomeItem>> get() = _printList

    //userItem
    var userId = getUserInfo()?.id ?: "UserId"
    var currentUser = getUserInfo()?.name
    var userInfo: MutableLiveData<UserItem?> = MutableLiveData()

    init {
        loadDataFromFirebase()
        getUserItem()
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

                _printList.value = list.value?.filter { item ->
                    circumLocation.contains(item.location)
                }
            }

            if ((printList.value?.size ?: 0) < 10) {
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
                    _printList.value = list.value?.filter { item ->
                        circumLocation.contains(item.location)
                    }

                }

                if ((printList.value?.size ?: 0) < 10) {
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
                            _printList.value = list.value?.filter { item ->
                                circumLocation.contains(item.location)
                            }
                        }

                        if ((printList.value?.size ?: 0) < 10) {
                            val existingList = _printList.value.orEmpty()
                            val filteredList = list.value?.filter { item ->
                                !circumLocation.contains(item.location)
                            }

                            val combinedList = existingList.toMutableList()
                                .apply { addAll(filteredList.orEmpty()) }
                            _printList.value = combinedList

                        }
                    }
                }
            }
        }.onFailure { e ->
            if (e is retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("Retrofit Error", "$TAG Request failed: code(${e.code()}), message($errorBody)")
            } else {
                Log.e("Retrofit Error", "$TAG Request failed: ${e.message}")
            }
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

    private fun loadDataFromFirebase() {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("home_list")
        databaseReference.orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val dataList = ArrayList<HomeItem>()

                    for (postSnapshot in dataSnapshot.children) {
                        val firebaseData = postSnapshot.getValue(HomeItem::class.java)
                        if (firebaseData != null) {
                            dataList.add(firebaseData)
                        }
                    }
                    _list.value = dataList.reversed()
                    _printList.value = dataList.reversed()
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }

    fun tagFilterList(tag: String) {
        val currentUserLocation = userInfo.value?.firstLocation ?: return
        val filteredList = if (tag.equals("All", ignoreCase = true)) {
            // "전체" 태그가 선택된 경우, 현재 사용자의 지역에 해당하는 모든 아이템을 표시
            list.value?.filter { it.location == currentUserLocation }
        } else {
            // 선택한 태그를 기반으로 아이템 필터링
            list.value?.filter {
                it.tag.equals(
                    tag,
                    ignoreCase = true
                ) && it.location == currentUserLocation
            }
        }
        _filteredItems.value = filteredList ?: emptyList()
    }

    fun setKeyword(keyword: String) {
        _searchKeyword.value = keyword
    }

    fun searchItems(keyword: String): List<HomeItem> {
        return list.value?.filter {
            it.title.contains(keyword, ignoreCase = true)
        } ?: listOf()
    }

    // 작동이 안되는 거였네..
    private fun getUserItem() = viewModelScope.launch {
        runCatching {
            userItem(userId).collect { user ->
                val userItem =
                    UserItem(
                        id = user?.id ?: "",
                        name = user?.name ?: "",
                        imgProfile = user?.imgProfile ?: "",
                        email = user?.email ?: "",
                        chatIds = user?.chatIds.orEmpty(),
                        groupIds = user?.groupIds.orEmpty(),        //모임
                        likedIds = user?.likedIds.orEmpty(),        //좋아요 게시물
                        writtenIds = user?.writtenIds.orEmpty(),        //작성한 게시물
                        firstLocation = user?.firstLocation ?: "",
                        secondLocation = user?.secondLocation ?: ""
                    )
                userInfo.postValue(userItem)
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
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

    fun resetToInitialList() {
        _printList.value = _list.value
    }

    fun updateUserLocation(
        firstLocation: String,
        secondLocation: String,
    ) = viewModelScope.launch {
        runCatching {
            userUpdateInfoUseCase(
                userId,
                userInfo.value?.imgProfile.toString(),
                userInfo.value?.name.toString(),
                firstLocation,
                secondLocation
            )
        }.onFailure {
            Log.e(TAG, it.message.toString())
        }
    }

    fun deleteItem(item: HomeItem) {
        // Firebase에서 항목 삭제
        val itemId = item.id // 항목의 고유 ID 또는 키
        deleteItemFromFirebase(itemId)
        userRemoveIdsUseCase(getUserInfo()?.id ?: "UserId", itemId, null, null, null)
        val updatedList = _list.value?.toMutableList() ?: mutableListOf()
        updatedList.remove(item)
        _list.value = updatedList

        // 필터링된 아이템 업데이트
        val filteredList = _filteredItems.value?.toMutableList() ?: mutableListOf()
        filteredList.remove(item)
        _filteredItems.value = filteredList

    }

    private fun deleteItemFromFirebase(itemId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("home_list")
        val itemReference = databaseReference.child(itemId)

        itemReference.removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loadDataFromFirebase()
                } else {
                    val exception = task.exception
                    if (exception != null) {
                        // 오류 처리 코드
                    }
                }
            }
    }
}

class HomeViewModelFactory(
    val context: Context,
) : ViewModelProvider.Factory {
    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)
    private val databaseReference = FirebaseDatabase.getInstance()

    val userPrefRepository = UserPreferencesRepositoryImpl(
        SignInPreferenceImpl(
            context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
        ),
        UserInfoPreferenceImpl(
            context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
        )
    )

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("user_list"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }
    private val circumLocationrepository: MapSearchRepository = MapSearchRepositoryImpl(
        KAKAORetrofitClient.search
    )


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                UserGetItemUseCase(userRepositoryImpl),
                UserUpdateInfoUseCase(userRepositoryImpl),
                MapSearchCircumLocationGetItemsUseCase(circumLocationrepository),
                MapSearchGetItemsUseCase(circumLocationrepository),
                UserRemoveIdsUseCase(userRepositoryImpl),
                UserPrefEditItemUseCase(userPrefRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}