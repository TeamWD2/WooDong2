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
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.GetFirebaseTokenUseCase
import com.wd.woodong2.domain.usecase.SignInGetUserUseCase
import com.wd.woodong2.domain.usecase.SignInSaveUserUseCase
import com.wd.woodong2.domain.usecase.UserSignInUseCase
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUser: UserSignInUseCase,
    private val saveUser: SignInSaveUserUseCase,
    private val getUser: SignInGetUserUseCase,
    private val getFirebaseToken: GetFirebaseTokenUseCase,
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

                        if (isSuccess && token != "") {
                            saveUser(id, isAutoLogIn)
//                            updateUserToken(id)
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
    private val context: Context,
) : ViewModelProvider.Factory {

    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }

    private val userPreferencesRepository by lazy {
        UserPreferencesRepositoryImpl(
            SignInPreferenceImpl(
                context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE)
            )
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(
                UserSignInUseCase(userRepositoryImpl),
                SignInSaveUserUseCase(userPreferencesRepository),
                SignInGetUserUseCase(userPreferencesRepository),
                GetFirebaseTokenUseCase(),
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}
