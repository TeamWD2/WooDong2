package com.wd.woodong2.presentation.mypage.content.written

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch

class MyPageWrittenViewModel (
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val userItem: UserGetItemUseCase,
    private val userRemoveIdsUseCase: UserRemoveIdsUseCase,
) : ViewModel() {
    companion object {
        private val TAG = "MyPageWrittenViewModel"
    }

    // 필터링된 아이템을 저장하는 LiveData
    private val _filteredItems = MutableLiveData<List<HomeItem>>()

    val filteredItems: LiveData<List<HomeItem>> = _filteredItems


    private val _list: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val list: LiveData<List<HomeItem>> get() = _list

    val _printList: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val printList: LiveData<List<HomeItem>> get() = _printList

    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> get() = _loadingState
    private val _isEmptyList: MutableLiveData<Boolean> = MutableLiveData()
    val isEmptyList: LiveData<Boolean> get() = _isEmptyList

    val userId= getUserInfo()?.id ?: "UserId"
    var userInfo: MutableLiveData<UserItem> = MutableLiveData()

    init {
        loadDataFromFirebase()
        getUser()
    }

    fun getUser() = viewModelScope.launch {
        _loadingState.value = true
        runCatching {
            userItem(userId).collect { user ->
                _printList.value = list.value?.filter { item ->
                    user?.writtenIds?.contains(item.id) == true
                }
                _isEmptyList.value = _printList.value?.isEmpty()
                _loadingState.value = false
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
            _loadingState.value = false
        }
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

                }

                override fun onCancelled(databaseError: DatabaseError) {
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
class MyPageWrittenViewModelFactory(
    val context: Context
) : ViewModelProvider.Factory {

    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)
    //private val databaseReference = FirebaseDatabase.getInstance()

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
        if (modelClass.isAssignableFrom(MyPageWrittenViewModel::class.java)) {
            return MyPageWrittenViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                UserGetItemUseCase(userRepositoryImpl),
                UserRemoveIdsUseCase(userRepositoryImpl),
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}