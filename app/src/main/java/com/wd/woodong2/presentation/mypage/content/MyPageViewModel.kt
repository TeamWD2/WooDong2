package com.wd.woodong2.presentation.mypage.content

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
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.data.sharedpreference.SignInPreferenceImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.usecase.user.UserGetItemUseCase
import com.wd.woodong2.domain.usecase.user.UserUpdateInfoUseCase
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.user.UserLogOutUseCase
import com.wd.woodong2.domain.usecase.prefs.UserPrefDeleteItemUseCase
import com.wd.woodong2.domain.usecase.prefs.UserPrefEditItemUseCase
import com.wd.woodong2.domain.usecase.prefs.UserPrefGetItemUseCase
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.home.content.HomeItem
import kotlinx.coroutines.launch

class MyPageViewModel(
    private val prefGetUserItem: UserPrefGetItemUseCase,
    private val userItem: UserGetItemUseCase,
    private val userUpdateInfoUseCase: UserUpdateInfoUseCase,
    private val userPrefDeleteUseCase: UserPrefDeleteItemUseCase,
    private val userLogOutUseCase: UserLogOutUseCase,
    private val userPrefEditItemUseCase: UserPrefEditItemUseCase,
) : ViewModel(

) {
    companion object {
        private val TAG = "MyPageViewModel"
    }

    private val _list: MutableLiveData<List<HomeItem>> = MutableLiveData()
    val list: LiveData<List<HomeItem>> get() = _list

    val userId = getUserInfo()?.id ?: "UserId"
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
                Log.d(TAG, userItem.toString())
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
        }
    }

    fun updateUserItem(
        name: String,
        imgProfile: String,
    ) = viewModelScope.launch {
        runCatching {
            userUpdateInfoUseCase(
                userId, imgProfile, name,
                userInfo.value?.firstLocation.toString(), userInfo.value?.secondLocation.toString()
            )
        }
    }

    fun editPrefUserInfo(
        name :String?,
        imgProfile : String?,
        firstLocation : String?,
        secondLocation: String?
    ) = userPrefEditItemUseCase(name,imgProfile,firstLocation,secondLocation)?.let{
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

    fun logout() {
        userPrefDeleteUseCase()
        userLogOutUseCase()
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

class MyPageViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)

    val userPrefRepository = UserPreferencesRepositoryImpl(
        null,
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

    private val userPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(
            SignInPreferenceImpl(
                context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
            ),
            UserInfoPreferenceImpl(
                context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
            )
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            return MyPageViewModel(
                UserPrefGetItemUseCase(userPrefRepository),
                UserGetItemUseCase(userRepositoryImpl),
                UserUpdateInfoUseCase(userRepositoryImpl),
                UserPrefDeleteItemUseCase(userPreferencesRepository),
                UserLogOutUseCase(userRepositoryImpl),
                UserPrefEditItemUseCase(userPrefRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }

    }
}