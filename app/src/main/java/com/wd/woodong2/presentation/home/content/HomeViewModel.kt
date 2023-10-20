package com.wd.woodong2.presentation.home.content

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.usecase.UserGetItemsUseCase
import com.wd.woodong2.presentation.chat.content.UserItem
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userItem: UserGetItemsUseCase
    ) : ViewModel(
    ) {

    private val _list: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val list: LiveData<List<HomeItem>> get() = _list

    val userId = "user1"
    val userInfo: MutableLiveData<UserItem> = MutableLiveData()

    init {
        getUserItem()
    }

    private fun getUserItem(

    ) = viewModelScope.launch {
        runCatching {
            userItem(userId) { items ->
                val userItem = items?.userItems?.map {
                    UserItem(
                        id = it.id,
                        name = it.name,
                        imgProfile = it.imgProfile,
                        email = it.email,
                        chatIds = it.chatIds,
                        firstLocation = it.firstLocation,
                        secondLocation = it.secondLocation
                    )
                }.orEmpty()
                userInfo.postValue(userItem.firstOrNull())
            }
        }.onFailure {
            Log.e("homeItem", it.message.toString())
        }

    }

}
class HomeViewModelFactory() : ViewModelProvider.Factory {

    private val userDatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("users")
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                UserGetItemsUseCase(UserRepositoryImpl(userDatabaseReference))
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}