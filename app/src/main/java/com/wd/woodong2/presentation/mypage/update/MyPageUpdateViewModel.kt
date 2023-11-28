package com.wd.woodong2.presentation.mypage.update

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.wd.woodong2.R
import com.wd.woodong2.data.repository.ImageStorageRepositoryImpl
import com.wd.woodong2.data.repository.UserPreferencesRepositoryImpl
import com.wd.woodong2.data.repository.UserRepositoryImpl
import com.wd.woodong2.data.sharedpreference.SignInPreferenceImpl
import com.wd.woodong2.data.sharedpreference.UserInfoPreferenceImpl
import com.wd.woodong2.domain.provider.FirebaseTokenProvider
import com.wd.woodong2.domain.usecase.ImageStorageSetItemUseCase
import com.wd.woodong2.domain.usecase.account.SignUpCheckNickNameDupUseCase
import com.wd.woodong2.domain.usecase.prefs.UserPrefDeleteItemUseCase
import com.wd.woodong2.domain.usecase.user.UserDeleteUserUseCase
import com.wd.woodong2.domain.usecase.user.UserUpdateInfoUseCase
import com.wd.woodong2.domain.usecase.user.UserUpdatePasswordUseCase
import com.wd.woodong2.presentation.group.detail.board.detail.GroupDetailBoardDetailViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class MyPageUpdateViewModel(
    private val userUpdateInfoUseCase: UserUpdateInfoUseCase,
    private val checkNicknameDup: SignUpCheckNickNameDupUseCase,
    private val imageStorageSetItem: ImageStorageSetItemUseCase,
    private val userUpdatePasswordUseCase: UserUpdatePasswordUseCase,
    private val userDeleteUser: UserDeleteUserUseCase,
    private val userPrefDeleteUseCase: UserPrefDeleteItemUseCase
) : ViewModel(
) {
    companion object {
        private val TAG = "MyPageUpdateViewModel"

        const val nicknamePattern = "^[a-zA-Z0-9가-힣_]+$"
        const val passwordPattern =
            "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{8,16}$"
    }

    private val _isNicknameDuplication: MutableLiveData<Boolean> = MutableLiveData()
    val isNicknameDuplication: LiveData<Boolean> get() = _isNicknameDuplication

    private val _isValidCurrentPassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidCurrentPassword: LiveData<Boolean> get() = _isValidCurrentPassword

    private val _isCheckCurrentPassword: MutableLiveData<Boolean> = MutableLiveData()
    val isCheckCurrentPassword: LiveData<Boolean> get() = _isCheckCurrentPassword

    private val _isValidPassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidPassword: LiveData<Boolean> get() = _isValidPassword

    private val _isValidSamePassword: MutableLiveData<Boolean> = MutableLiveData()
    val isValidSamePassword: LiveData<Boolean> get() = _isValidSamePassword

    val _isValidNickname: MutableLiveData<Boolean> = MutableLiveData()
    val isValidNickname: LiveData<Boolean> get() = _isValidNickname

    var ImgProfile: Uri? = null

    val _isValidImg: MutableLiveData<Boolean> = MutableLiveData()
    val isValidImg: LiveData<Boolean> get() = _isValidImg

    val _setResult: MutableLiveData<Boolean> = MutableLiveData()
    val setResult: LiveData<Boolean> get() = _setResult

    // 회원 탈퇴
    fun deleteMember(userId: String?) {
        if(userId == null) {
            return
        }
        viewModelScope.launch {
            runCatching {
                userDeleteUser(
                    userId
                )
                userPrefDeleteUseCase() //SharedPreferences 삭제
            }.onFailure {
                Log.e(TAG, it.message.toString())
            }
        }
    }

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

    fun checkValidCurrentPassword(userEmail: String, currentPw: String){
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(userEmail, currentPw)
            .addOnCompleteListener { task ->
                _isCheckCurrentPassword.value = task.isSuccessful
            }
    }

    fun setProfileImage(uri: Uri) = viewModelScope.launch {
        runCatching {
            imageStorageSetItem(uri).collect { imageUri ->
                ImgProfile = imageUri
            }
            _isValidImg.value = true
        }.onFailure {
            Log.e(TAG, it.message.toString())
            _isValidImg.value = false
        }
    }

    // 모든 요소 판단 메소드
    fun checkAllConditions(): Boolean {
        return isValidCurrentPassword.value == true
                && isCheckCurrentPassword.value == true
                    && isValidPassword.value == true
                    && isValidSamePassword.value == true
                    && isValidNickname.value == true
                    && isNicknameDuplication.value == false
                    && isValidImg.value == true

    }

    //작성완료 메소드
    fun editInfo(
        userId: String,
        imgProfile: String?,
        name: String?,
        firstLocation: String,
        secondLocation: String,
        passwordJudge: Boolean,
        email: String?,
        currentPW: String?,
        changePW: String?
    ) {
        viewModelScope.launch {
            try {
                if (checkAllConditions() && passwordJudge) {
                    userUpdateInfoUseCase(
                        userId,
                        imgProfile.toString(),
                        name.toString(),
                        firstLocation,
                        secondLocation
                    )
                    userUpdatePasswordUseCase(
                        email.toString(),
                        currentPW.toString(),
                        changePW.toString()
                    )

                    _setResult.value = true
                }
                else if(passwordJudge){
                    userUpdatePasswordUseCase(
                        email.toString(),
                        currentPW.toString(),
                        changePW.toString()
                    )

                    _setResult.value = true
                }
                else{
                    userUpdateInfoUseCase(
                        userId,
                        imgProfile.toString(),
                        name.toString(),
                        firstLocation,
                        secondLocation)
                    _setResult.value = true
                }

            } catch (e: Exception) {
                // 에러 처리
                _setResult.value = false
            }
        }
    }
}

class MyPageUpdateViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory {
    private val userRepositoryImpl by lazy {
        UserRepositoryImpl(
            FirebaseDatabase.getInstance().getReference("user_list"),
            Firebase.auth,
            FirebaseTokenProvider(FirebaseMessaging.getInstance())
        )
    }

    private val userPrefKey = context.getString(R.string.pref_key_user_preferences_key)
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

    private val imageStorageRepository =
        ImageStorageRepositoryImpl(FirebaseStorage.getInstance().reference)//.child("images/${UUID.randomUUID()}")

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageUpdateViewModel::class.java)) {
            return MyPageUpdateViewModel(
                UserUpdateInfoUseCase(userRepositoryImpl),
                SignUpCheckNickNameDupUseCase(userRepositoryImpl),
                ImageStorageSetItemUseCase(imageStorageRepository),
                UserUpdatePasswordUseCase(userRepositoryImpl),
                UserDeleteUserUseCase(userRepositoryImpl),
                UserPrefDeleteItemUseCase(userPreferencesRepository)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}