package com.wd.woodong2.presentation.mypage.update

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
import com.wd.woodong2.domain.usecase.UserUpdateInfoUseCase
import com.wd.woodong2.domain.usecase.UserUpdatePasswordUseCase
import com.wd.woodong2.presentation.signup.SignUpViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.regex.Pattern

class MyPageUpdateViewModel(
    private val userUpdateInfoUseCase: UserUpdateInfoUseCase,
    private val checkNicknameDup: SignUpCheckNickNameDupUseCase,
    private val imageStorageSetItem: ImageStorageSetItemUseCase,
): ViewModel(
)  {
    companion object {
        const val nicknamePattern = "^[a-zA-Z0-9가-힣_]+$"
        const val passwordPattern =
            "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"

    }

    private val _isNicknameDuplication: MutableLiveData<Boolean> = MutableLiveData()
    val isNicknameDuplication: LiveData<Boolean> get() = _isNicknameDuplication

    private val _isValidCurrentPassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidCurrentPassword: LiveData<Boolean> get() = _isValidCurrentPassword

    private val _isValidPassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidPassword: LiveData<Boolean> get() = _isValidPassword

    private val _isValidSamePassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidSamePassword: LiveData<Boolean> get() = _isValidSamePassword

    private val _isValidNickname: MutableLiveData<Boolean> = MutableLiveData()
    val isValidNickname: LiveData<Boolean> get() = _isValidNickname

    private var imgProfile: Uri? = null

    private val _setResult: MutableLiveData<Any> = MutableLiveData()
    val setResult: LiveData<Any> get() = _setResult

    private var passwordJudge : Boolean = false

    // 닉네임 중복 판단 메소드
    fun checkNicknameDuplication(nickname: String) {
        viewModelScope.launch {
            val result = checkNicknameDup(nickname)
            _isNicknameDuplication.value = result
        }
    }

    // Nickname 유효성 판단 메소드
    fun checkValidNickname(nickname: String) {
        _isValidNickname.value =
            nickname.isNotEmpty() && nickname.length in 2..10 && nickname.matches(nicknamePattern.toRegex())
    }

    // PW 유효성 판단 메소드
    fun checkValidPassword(currentpw: String, pw: String) {
        _isValidCurrentPassword.value = currentpw != pw
        _isValidPassword.value = Pattern.matches(passwordPattern, pw)
    }

    // PW 동일성 판단 메소드
    fun checkValidSamePassword(originalPw: String, copyPw: String) {
        _isValidSamePassword.value = originalPw == copyPw
    }

    fun setProfileImage(uri: Uri) = viewModelScope.launch {
        runCatching {
            imageStorageSetItem(uri).collect { imageUri ->
                imgProfile = imageUri
            }
        }.onFailure {
            Log.e(SignUpViewModel.TAG, it.message.toString())
        }
    }

    // 모든 요소 판단 메소드
    fun checkAllConditions(): Boolean {
        return if(passwordJudge){
            (isValidCurrentPassword.value == true
                    && isValidPassword.value == true
                    && isValidSamePassword.value == true
                    && isValidNickname.value == true
                    && isNicknameDuplication.value == false)
        } else{
            (isValidNickname.value == true
                    && isNicknameDuplication.value == false)
        }
    }

    //작성완료 메소드
    fun editInfo(
        userId: String,
        email: String,
        currentPassword: String?,
        newPassword: String?,
        imgProfile: String?,
        name: String?,
        firstLocation: String,
        secondLocation: String,
        passwordJudge: Boolean,
        ) {
        viewModelScope.launch {
            try {
                Log.d("mypage",name.toString())

                this@MyPageUpdateViewModel.passwordJudge = passwordJudge

                userUpdateInfoUseCase(userId,
                    imgProfile.toString(), name.toString(), firstLocation, secondLocation)

                    _setResult.value = true
            } catch (e: Exception) {
                // 에러 처리
                _setResult.value = "ERROR: ${e.message}"
            }
        }
    }
}
class MyPageUpdateViewModelFactory : ViewModelProvider.Factory {
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
        if (modelClass.isAssignableFrom(MyPageUpdateViewModel::class.java)) {
            return MyPageUpdateViewModel(
                UserUpdateInfoUseCase(userRepositoryImpl),
                SignUpCheckNickNameDupUseCase(userRepositoryImpl),
                ImageStorageSetItemUseCase(imageStorageRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}