package com.wd.woodong2.presentation.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.usecase.UserGetItemUseCase
import com.wd.woodong2.domain.usecase.UserSignUpUseCase
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SignUpViewModel(
    private val getUser: UserGetItemUseCase,
    private val signUpUser: UserSignUpUseCase,
) : ViewModel(
) {
    companion object {
        const val TAG = "SignUpViewModel"

        const val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        const val nicknamePattern = "^[a-zA-Z0-9가-힣_]+$"
        const val passwordPattern =
            "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"
    }

    private val _isDuplication: MutableLiveData<Boolean> = MutableLiveData()
    val isDuplication: LiveData<Boolean> get() = _isDuplication

    private val _isValidId: MutableLiveData<Boolean> = MutableLiveData()
    val isValidId: LiveData<Boolean> get() = _isValidId

    private val _isValidPassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidPassword: LiveData<Boolean> get() = _isValidPassword

    private val _isValidSamePassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidSamePassword: LiveData<Boolean> get() = _isValidSamePassword

    private val _isValidNickname: MutableLiveData<Boolean> = MutableLiveData()
    val isValidNickname: LiveData<Boolean> get() = _isValidNickname

    val isAllCorrect: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(isDuplication) { value = checkAllConditions() }
        addSource(isValidId) { value = checkAllConditions() }
        addSource(isValidPassword) { value = checkAllConditions() }
        addSource(isValidSamePassword) { value = checkAllConditions() }
        addSource(isValidNickname) { value = checkAllConditions() }
    }

    // ID 유효성 판단 메소드
    fun isValidId(id: String) {
        viewModelScope.launch {
            getUser(id).collect { item ->
                _isDuplication.value = item != null
                _isValidId.value =
                    id.isNotEmpty() && id.length in 5..18 && !id.contains(" ") && id.matches(
                        emailPattern.toRegex()
                    )
            }
        }
    }

    // PW 유효성 판단 메소드
    fun isValidPassword(pw: String) {
        _isValidPassword.value = Pattern.matches(passwordPattern, pw)
    }

    // PW 동일성 판단 메소드
    fun isValidSamePassword(originalPw: String, copyPw: String) {
        _isValidSamePassword.value = originalPw == copyPw
    }

    // Nickname 유효성 판단 메소드
    fun isValidNickname(nickname: String) {
        _isValidNickname.value =
            nickname.isNotEmpty() && nickname.length in 2..10 && nickname.matches(nicknamePattern.toRegex())
    }

    // 모든 요소 판단 메소드
    private fun checkAllConditions(): Boolean {

        Log.d(
            TAG,
            "${isDuplication.value}, ${isValidId.value}, ${isValidPassword.value}, ${isValidSamePassword.value}, ${isValidNickname.value}"
        )

        return !(isDuplication.value == true
                && isValidId.value == true
                && isValidPassword.value == true
                && isValidSamePassword.value == true
                && isValidNickname.value == true)
    }

    suspend fun signUp(id: String, pw: String, name: String) {
        signUpUser(id, pw, name)
    }
}

class SignUpViewModelFactory : ViewModelProvider.Factory {

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
            Firebase.auth
        )
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(
                UserGetItemUseCase(userRepositoryImpl),
                UserSignUpUseCase(userRepositoryImpl)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}