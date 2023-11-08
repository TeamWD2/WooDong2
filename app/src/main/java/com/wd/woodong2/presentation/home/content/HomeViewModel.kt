package com.wd.woodong2.presentation.home.content

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
import com.wd.woodong2.data.repository.MapSearchRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.model.MapSearchEntity
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.repository.MapSearchRepository
import com.wd.woodong2.domain.usecase.MapSearchCircumLocationGetItemsUseCase
import com.wd.woodong2.domain.usecase.MapSearchGetItemsUseCase
import com.wd.woodong2.domain.usecase.UserGetItemUseCase
import com.wd.woodong2.domain.usecase.UserUpdateInfoUseCase
import com.wd.woodong2.presentation.home.map.HomeMapActivity
import com.wd.woodong2.presentation.home.map.HomeMapSearchItem
import com.wd.woodong2.retrofit.KAKAORetrofitClient

class HomeViewModel(
    private val userItem: UserGetItemUseCase,
    private val userUpdateInfoUseCase: UserUpdateInfoUseCase,
    private val circumLocationItem: MapSearchCircumLocationGetItemsUseCase,
    private val mapSearch: MapSearchGetItemsUseCase
) : ViewModel(
) {

    private val _list: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val list: LiveData<List<HomeItem>> get() = _list


    private val _circumLocationList: MutableLiveData<List<HomeMapSearchItem>> = MutableLiveData()
    val circumLocationList: LiveData<List<HomeMapSearchItem>> get() = _circumLocationList

    private val _originalList: MutableLiveData<List<HomeItem>> = MutableLiveData()  // 원본 리스트 저장
    val originalList: LiveData<List<HomeItem>> get() = _originalList

    private val _searchResults = MutableLiveData<List<HomeItem>>()
    val searchResults: LiveData<List<HomeItem>> = _searchResults

    //주변 위치 값 받아오기
    var circumLocation = mutableSetOf<String>()

    //Home에 출력할 list 설정하기
    val _printList: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val printList: LiveData<List<HomeItem>> get() = _printList

    //userItem
    val userId = "user1"
    val userInfo: MutableLiveData<UserItem> = MutableLiveData()

    init {
        getUserItem()
        loadDataFromFirebase()
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

            _circumLocationList.postValue(circumLocationItems)
            circumLocationItems = createCircumLocationItems(
                Map = mapSearch(query)
            )
            _circumLocationList.postValue(circumLocationItems)

            Log.d("locationCheckci1", circumLocation.toString())

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

            if (printList.value?.size!! < 10) {
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
                Log.d("locationCheckci2", circumLocation.toString())

                if (circumLocation.isNotEmpty()) {

                    _printList.value = list.value?.filter { item ->
                        circumLocation.contains(item.location)
                    }

                }

                if (printList.value?.size!! < 10) {
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
                    Log.d("locationCheckci3", circumLocation.toString())

                    if (circumLocation.isNotEmpty()) {
                        for (loc in circumLocation) {
                            _printList.value = list.value?.filter { item ->
                                circumLocation.contains(item.location)
                            }
                        }

                        if (printList.value?.size!! < 10) {
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
            Log.e("Retrofit Error", "Request failed: ${e.message}")
        }
    }

    private fun createCircumLocationItems(
        Map: MapSearchEntity
    ): List<HomeMapSearchItem> {
        fun createMapSearchItems(
            Map: MapSearchEntity
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
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = ArrayList<HomeItem>()

                for (postSnapshot in dataSnapshot.children) {
                    val firebaseData = postSnapshot.getValue(HomeItem::class.java)
                    if (firebaseData != null) {
                        dataList.add(firebaseData)
                    }
                }
                //최신게시물 가장 위로 오게
                _originalList.value = dataList.reversed()
                _list.value = dataList.reversed()

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

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
            Log.e("homeItem", it.message.toString())
        }
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
            Log.e("locationhv", it.message.toString())
        }
    }

}

class HomeViewModelFactory : ViewModelProvider.Factory {

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
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
                UserGetItemUseCase(userRepositoryImpl),
                UserUpdateInfoUseCase(userRepositoryImpl),
                MapSearchCircumLocationGetItemsUseCase(circumLocationrepository),
                MapSearchGetItemsUseCase(circumLocationrepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}