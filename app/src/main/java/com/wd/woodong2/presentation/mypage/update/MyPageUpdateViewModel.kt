package com.wd.woodong2.presentation.mypage.update

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.usecase.UserGetItemUseCase
import com.wd.woodong2.domain.usecase.UserSignUpUseCase
import com.wd.woodong2.presentation.signup.SignUpViewModel
import java.util.regex.Pattern

class MyPageUpdateViewModel(

): ViewModel(
)  {
    companion object {
        const val nicknamePattern = "^[a-zA-Z0-9가-힣_]+$"
        const val passwordPattern =
            "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"

    }
    private val _isValidPassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidPassword: LiveData<Boolean> get() = _isValidPassword

    private val _isValidSamePassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidSamePassword: LiveData<Boolean> get() = _isValidSamePassword

    private val _isValidNickname: MutableLiveData<Boolean> = MutableLiveData()
    val isValidNickname: LiveData<Boolean> get() = _isValidNickname

    val isAllCorrect: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(isValidNickname) { value = checkAllConditions() }
        addSource(isValidPassword) { value = checkAllConditions() }
        addSource(isValidSamePassword) { value = checkAllConditions() }
    }
    // Nickname 유효성 판단 메소드
    fun isValidNickname(nickname: String) {
        _isValidNickname.value =
            nickname.isNotEmpty() && nickname.length in 2..10 && nickname.matches(MyPageUpdateViewModel.nicknamePattern.toRegex())
    }

    // PW 유효성 판단 메소드
    fun isValidPassword(pw: String) {
        _isValidPassword.value = Pattern.matches(MyPageUpdateViewModel.passwordPattern, pw)
    }

    // PW 동일성 판단 메소드
    fun isValidSamePassword(originalPw: String, copyPw: String) {
        _isValidSamePassword.value = originalPw == copyPw
    }

    // 모든 요소 판단 메소드
    private fun checkAllConditions(): Boolean {

        return !(isValidNickname.value == true
                && isValidSamePassword.value == true
                && isValidPassword.value == true)
    }
}
class MyPageUpdateViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageUpdateViewModel::class.java)) {
            return MyPageUpdateViewModel(

            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}