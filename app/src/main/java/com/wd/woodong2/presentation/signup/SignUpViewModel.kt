package com.wd.woodong2.presentation.signup

import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import com.wd.woodong2.data.repository.ImageStorageRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.ImageStorageSetItemUseCase
import com.wd.woodong2.domain.usecase.SignUpCheckNickNameDupUseCase
import com.wd.woodong2.domain.usecase.UserSignUpUseCase
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.regex.Pattern

class SignUpViewModel(
    private val signUpUser: UserSignUpUseCase,
    private val checkNicknameDup: SignUpCheckNickNameDupUseCase,
    private val imageStorageSetItem: ImageStorageSetItemUseCase,
) : ViewModel(
) {
    companion object {
        const val TAG = "SignUpViewModel"

        const val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+\\.?[a-zA-Z]*"
        const val nicknamePattern = "^[a-zA-Z0-9가-힣_]+$"
        const val passwordPattern =
            "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"
    }

    private val _signUpResult: MutableLiveData<Any> = MutableLiveData()
    val signUpResult: LiveData<Any> get() = _signUpResult


    private val _isNicknameDuplication: MutableLiveData<Boolean> = MutableLiveData()
    val isNicknameDuplication: LiveData<Boolean> get() = _isNicknameDuplication

    private val _isValidId: MutableLiveData<Boolean> = MutableLiveData()
    val isValidId: LiveData<Boolean> get() = _isValidId

    private val _isValidPassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidPassword: LiveData<Boolean> get() = _isValidPassword

    private val _isValidSamePassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidSamePassword: LiveData<Boolean> get() = _isValidSamePassword

    private val _isValidNickname: MutableLiveData<Boolean> = MutableLiveData()
    val isValidNickname: LiveData<Boolean> get() = _isValidNickname

    private var imgProfile: Uri? = null

    // 닉네임 중복 판단 메소드
    fun checkNicknameDuplication(nickname: String) {
        viewModelScope.launch {
            val result = checkNicknameDup(nickname)
            _isNicknameDuplication.value = result
        }
    }

    fun checkValidId(id: String) {
        _isValidId.value =
            id.isNotEmpty() && id.length in 5..22 && !id.contains(" ") && Pattern.matches(
                emailPattern,
                id
            )
    }

    // PW 유효성 판단 메소드
    fun checkValidPassword(pw: String) {
        _isValidPassword.value = Pattern.matches(passwordPattern, pw)
    }

    // PW 동일성 판단 메소드
    fun checkValidSamePassword(originalPw: String, copyPw: String) {
        _isValidSamePassword.value = originalPw == copyPw
    }

    // Nickname 유효성 판단 메소드
    fun checkValidNickname(nickname: String) {
        _isValidNickname.value =
            nickname.isNotEmpty() && nickname.length in 2..10 && nickname.matches(nicknamePattern.toRegex())
    }

    // 모든 요소 판단 메소드
    fun checkAllConditions(): Boolean {
        return isValidId.value == true
                && isValidPassword.value == true
                && isValidSamePassword.value == true
                && isValidNickname.value == true
                && isNicknameDuplication.value == false
    }

    /*
    * 회원가입 메소드
    * */
    fun signUp(id: String, pw: String, name: String) {
        viewModelScope.launch {
            try {
                signUpUser(id, pw, name, imgProfile)
                    .collect { result ->
                        // 성공 처리
                        _signUpResult.value = result
                    }
            } catch (e: Exception) {
                // 에러 처리
                _signUpResult.value = "ERROR: ${e.message}"
            }
        }
    }

    /**
     * 이미지 설정 메소드*/
    fun setProfileImage(uri: Uri) = viewModelScope.launch {
        runCatching {
            imageStorageSetItem(uri).collect { imageUri ->
                imgProfile = imageUri
            }
        }.onFailure {
            Log.e(TAG, it.message.toString())
        }
    }
}

class SignUpViewModelFactory : ViewModelProvider.Factory {

    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("users"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }

    private val imageStorageRepository =
        ImageStorageRepositoryImpl(FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}"))

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(
                UserSignUpUseCase(userRepositoryImpl),
                SignUpCheckNickNameDupUseCase(userRepositoryImpl),
                ImageStorageSetItemUseCase(imageStorageRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}