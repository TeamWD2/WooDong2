package com.wd.woodong2.presentation.signin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.wd.woodong2.R
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.usecase.SignInGetUserUseCase
import com.wd.woodong2.domain.usecase.SignInSaveUserUseCase
import com.wd.woodong2.domain.usecase.UserSignInUseCase
import com.wd.woodong2.presentation.provider.ContextProvider
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUser: UserSignInUseCase,
    private val saveUser: SignInSaveUserUseCase,
    private val getUser: SignInGetUserUseCase,
) : ViewModel(
) {
    companion object {
        const val TAG = "SignInViewModel"
    }

    private val _loginResult: MutableLiveData<Boolean> = MutableLiveData()
    val loginResult: LiveData<Boolean> get() = _loginResult


    fun signIn(id: String, pw: String, isAutoLogIn: Boolean) {
        viewModelScope.launch {
            runCatching {
                signInUser(id, pw).collect { isSuccess ->
                    _loginResult.value = isSuccess

                    // 기기에 정보 저장
                    if (_loginResult.value == true) {
                        saveUser(id, isAutoLogIn)
                    }
                }
            }.onFailure {
                Log.e(TAG, it.message.toString())
                _loginResult.value = false
            }
        }
    }

    fun isAutoLogin(): String? {
        if (getUser() != null) {
            return getUser()
        }
        return null
    }
}

class SignInViewModelFactory(
    private val contextProvider: ContextProvider,
) : ViewModelProvider.Factory {

    private val userPrefKey = contextProvider.getString(R.string.pref_key_user_preferences_key)

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
            Firebase.auth
        )
    }

    private val userPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(contextProvider.getSharedPreferences(userPrefKey))
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(
                UserSignInUseCase(userRepositoryImpl),
                SignInSaveUserUseCase(userPreferencesRepository),
                SignInGetUserUseCase(userPreferencesRepository),
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}
