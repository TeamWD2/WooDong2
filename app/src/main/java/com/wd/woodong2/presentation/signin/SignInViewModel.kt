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
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.usecase.UserSignInUseCase
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signInUser: UserSignInUseCase,
) : ViewModel(
) {
    companion object {
        const val TAG = "SignInViewModel"
    }

    private val _loginResult: MutableLiveData<Boolean> = MutableLiveData()
    val loginResult: LiveData<Boolean> get() = _loginResult


    fun signIn(id: String, pw: String) {
        viewModelScope.launch {
            runCatching {
                signInUser(id, pw).collect { isSuccess ->
                    _loginResult.value = isSuccess
                }
            }.onFailure {
                Log.e(TAG, it.message.toString())
                _loginResult.value = false
            }
        }
    }
}

class SignInViewModelFactory : ViewModelProvider.Factory {

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
            Firebase.auth
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(
                UserSignInUseCase(userRepositoryImpl)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}
