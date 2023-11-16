package com.wd.woodong2.presentation.mypage.content.thumb

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.wd.woodong2.R
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.data.sharedpreference.SignInPreferenceImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.user.UserGetItemUseCase
import com.wd.woodong2.domain.usecase.prefs.UserPrefGetItemUseCase
import com.wd.woodong2.domain.usecase.user.UserRemoveIdsUseCase
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.home.content.HomeItem

class MyPageThumbViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val userItem: UserGetItemUseCase,
    private val userRemoveIdsUseCase: UserRemoveIdsUseCase,
) : ViewModel() {

    // 필터링된 아이템을 저장하는 LiveData
    private val _filteredItems = MutableLiveData<List<HomeItem>>()

    val filteredItems: LiveData<List<HomeItem>> = _filteredItems

    private val _list: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val list: LiveData<List<HomeItem>> get() = _list

    private val _printList: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val printList: LiveData<List<HomeItem>> get() = _printList
    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> get() = _loadingState
    private val _isEmptyList: MutableLiveData<Boolean> = MutableLiveData()
    val isEmptyList: LiveData<Boolean> get() = _isEmptyList

    val userId = getUserInfo()?.id ?: "UserId"
    var userInfo: MutableLiveData<UserItem> = MutableLiveData()

    init {
        loadDataFromFirebase()
    }

    private fun loadDataFromFirebase() {
        _loadingState.value = true  // 데이터 로딩 시작

        val databaseReference = FirebaseDatabase.getInstance().reference.child("home_list")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = ArrayList<HomeItem>()
                for (postSnapshot in dataSnapshot.children) {
                    val firebaseData = postSnapshot.getValue(HomeItem::class.java)
                    if (firebaseData != null && userId in firebaseData.likedBy) {
                        dataList.add(firebaseData)
                    }
                }
                _list.value = dataList.reversed()
                _printList.value = dataList.reversed()
                _isEmptyList.value = dataList.isEmpty()
                _loadingState.value = false  // 데이터 로딩 완료
            }

            override fun onCancelled(databaseError: DatabaseError) {
                _loadingState.value = false  // 데이터 로딩 실패 또는 취소
            }
        })
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

class MyPageThumbViewModelFactory(
    val context: Context
) : ViewModelProvider.Factory {
    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)

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

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageThumbViewModel::class.java)) {
            return MyPageThumbViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                UserGetItemUseCase(userRepositoryImpl),
                UserRemoveIdsUseCase(userRepositoryImpl),
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}