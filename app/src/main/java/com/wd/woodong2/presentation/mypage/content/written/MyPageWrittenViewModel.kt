package com.wd.woodong2.presentation.mypage.content.written

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
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.UserGetItemsUseCase
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.home.content.HomeItem
import com.wd.woodong2.presentation.mypage.content.thumb.MyPageThumbViewModel
import com.wd.woodong2.presentation.mypage.content.thumb.MyPageThumbViewModelFactory
import kotlinx.coroutines.launch

class MyPageWrittenViewModel (
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

    val userId= "user1"
    private var userInfo: MutableLiveData<UserItem> = MutableLiveData()

    init {
        getUserItem()
        loadDataFromFirebase()

        printListSet()
    }
    private fun printListSet() = viewModelScope.launch{
        var checkCount = 0
        _loadingState.value = true
        runCatching {
            for (writtenIds in userInfo.value?.writtenIds!!) {
                val existingList = _printList.value.orEmpty()
                val filteredList = list.value?.filter { it.id == writtenIds }
                checkCount++
                if (checkCount == userInfo.value?.writtenIds!!.size) {
                    _isEmptyList.value = _printList.value?.isEmpty()
                    val combinedList =
                        existingList.toMutableList().apply { addAll(filteredList!!) }
                    _printList.value = combinedList
                }
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
        MyPageWrittenViewModelFactory().databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = ArrayList<HomeItem>()

                for (postSnapshot in dataSnapshot.children) {
                    val firebaseData = postSnapshot.getValue(HomeItem::class.java)
                    if (firebaseData != null) {
                        dataList.add(firebaseData)
                    }
                }
                _list.value = dataList
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}
class MyPageWrittenViewModelFactory : ViewModelProvider.Factory {
    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }
    val databaseReference by lazy {
        FirebaseDatabase.getInstance().reference.child("home_list")
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageWrittenViewModel::class.java)) {
            return MyPageWrittenViewModel(
                UserGetItemsUseCase(userRepositoryImpl),
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}