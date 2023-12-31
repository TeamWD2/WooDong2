package com.wd.woodong2.presentation.signin

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
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.GetFirebaseTokenUseCase
import com.wd.woodong2.domain.usecase.account.SignInGetUserUIDUseCase
import com.wd.woodong2.domain.usecase.account.SignInGetUIDUseCase
import com.wd.woodong2.domain.usecase.account.SignInSaveUserUseCase
import com.wd.woodong2.domain.usecase.user.UserGetItemUseCase
import com.wd.woodong2.domain.usecase.prefs.UserPrefSetItemUseCase
import com.wd.woodong2.domain.usecase.user.UserSignInUseCase
import com.wd.woodong2.domain.usecase.user.UserUpdateTokenUseCase
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUser: UserSignInUseCase,
    private val saveUser: SignInSaveUserUseCase,
    private val getUID: SignInGetUIDUseCase,
    private val getFirebaseToken: GetFirebaseTokenUseCase,
    private val getUserUID: SignInGetUserUIDUseCase,
    private val getUserItem: UserGetItemUseCase,
    private val prefSetUserItem: UserPrefSetItemUseCase,
    private val updateTokenUseCase: UserUpdateTokenUseCase,
) : ViewModel(
) {
    companion object {
        const val TAG = "SignInViewModel"
    }

    private val _loginResult: MutableLiveData<Boolean> = MutableLiveData()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _isSetUserInfo: MutableLiveData<Boolean> = MutableLiveData()
    val isSetUserInfo: LiveData<Boolean> get() = _isSetUserInfo


    fun signIn(id: String, pw: String, isAutoLogIn: Boolean) {
        viewModelScope.launch {
            runCatching {
                signInUser(id, pw)
                    .flatMapConcat { isSuccess ->
                        if (isSuccess) {
                            getFirebaseToken().map { token -> Pair(isSuccess, token) }
                        } else {
                            flowOf(Pair(false, ""))
                        }
                    }
                    .collect { (isSuccess, token) ->
                        _loginResult.value = isSuccess

                        val uid = getUserUID()

                        if (isSuccess && token != "" && uid != null) {
                            saveUser(id, isAutoLogIn, uid)
                        }
                    }
            }.onFailure {
                Log.e(TAG, it.message.toString())
                _loginResult.value = false
            }
        }
    }

    /*
    * SharedPreference에서
    *  저장된 유저
    * 정보 가져오기*/
    fun isAutoLogin(): String? {
        if (getUID() != null) {
            return getUID()
        }
        return null
    }

    /*
    * Auth에서
    * UID 가져오기 */
    fun getUserUIDFromAuth(): String? {
        if (getUserUID() != null) {
            return getUserUID()
        }
        return null
    }

    fun setUserInfo(uid: String) = viewModelScope.launch {
        runCatching {
            getUserItem(uid).collect { user ->
                if (user != null) {
                    prefSetUserItem(user)
                    _isSetUserInfo.value = true
                }
            }
        }.onFailure {
            _isSetUserInfo.value = false
        }
    }

    fun updateToken(uid: String) = viewModelScope.launch {
        updateTokenUseCase(uid)
    }
}

class SignInViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {

    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)

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
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(
                UserSignInUseCase(userRepositoryImpl),
                SignInSaveUserUseCase(userPreferencesRepository),
                SignInGetUIDUseCase(userPreferencesRepository),
                GetFirebaseTokenUseCase(),
                SignInGetUserUIDUseCase(userRepositoryImpl),
                UserGetItemUseCase(userRepositoryImpl),
                UserPrefSetItemUseCase(userPreferencesRepository),
                UserUpdateTokenUseCase(userRepositoryImpl)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}
