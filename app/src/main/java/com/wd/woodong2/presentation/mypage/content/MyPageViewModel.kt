package com.wd.woodong2.presentation.mypage.content

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
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.UserGetItemsUseCase
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.home.content.HomeItem
import kotlinx.coroutines.launch

class MyPageViewModel(
    private val userItem: UserGetItemsUseCase,
) : ViewModel(

) {
    private val _list: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val list: LiveData<List<HomeItem>> get() = _list

    val userId = "user1"
    var userInfo: MutableLiveData<UserItem> = MutableLiveData()

    init {
        getUserItem()
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

    fun updateUserItem(
        name: String,
        imgProfile: String,
        email: String,
    ) = viewModelScope.launch {
        runCatching {
            userItem(userId, name, imgProfile, email)
        }
    }
}

class MyPageViewModelFactory : ViewModelProvider.Factory {

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            return MyPageViewModel(
                UserGetItemsUseCase(userRepositoryImpl),
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}