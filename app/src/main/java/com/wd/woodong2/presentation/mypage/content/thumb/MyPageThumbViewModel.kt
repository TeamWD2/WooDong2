package com.wd.woodong2.presentation.mypage.content.thumb


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
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.UserGetItemsUseCase
import com.wd.woodong2.domain.usecase.UserPrefGetItemUseCase
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.home.content.HomeItem
import kotlinx.coroutines.launch

class MyPageThumbViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val userItem: UserGetItemsUseCase,
) : ViewModel(){
    private val _list: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val list: LiveData<List<HomeItem>> get() = _list

    private val _printList: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val printList: LiveData<List<HomeItem>> get() = _printList
    private val _loadingState: MutableLiveData<Boolean> = MutableLiveData()
    val loadingState: LiveData<Boolean> get() = _loadingState
    private val _isEmptyList: MutableLiveData<Boolean> = MutableLiveData()
    val isEmptyList: LiveData<Boolean> get() = _isEmptyList

    val userId = getUserInfo()?.id ?: "UserId"
    private var userInfo: MutableLiveData<UserItem> = MutableLiveData()

    init {
        getUserItem()
        loadDataFromFirebase()
    }

    fun printListSet()= viewModelScope.launch{
        _loadingState.value = true
        runCatching {
            _isEmptyList.value = _printList.value?.isEmpty()
            _printList.value = list.value?.filter { item ->
                userInfo.value?.likedIds!!.contains(item.id)
            }
            _loadingState.value = false
        }.onFailure {
            _loadingState.value = false
            Log.e("Item", "비어있음")
        }
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
}
class MyPageThumbViewModelFactory(
    val context: Context
) : ViewModelProvider.Factory {

    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)
    //private val databaseReference = FirebaseDatabase.getInstance()

    val userPrefRepository = UserPreferencesRepositoryImpl(
        null,
        UserInfoPreferenceImpl(
            context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
        )
    )


    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageThumbViewModel::class.java)) {
            return MyPageThumbViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                UserGetItemsUseCase(userRepositoryImpl),
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}