package com.wd.woodong2.presentation.mypage.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.wd.woodong2.R
import com.wd.woodong2.databinding.MyPageUpdateActivityBinding
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.home.map.HomeMapActivity
import com.wd.woodong2.presentation.mypage.content.MyPageFragment
import com.wd.woodong2.presentation.signin.SignInActivity
import kotlinx.coroutines.launch


class MyPageUpdateActivity : AppCompatActivity() {

    companion object {
        lateinit var userInfo: UserItem

        fun newIntent(context: Context, userItem: UserItem) =
            Intent(context, MyPageUpdateActivity::class.java).apply {
                userInfo = userItem
            }
    }

    private var profile = userInfo.imgProfile
    private var name = userInfo.name
    private var passwordJudge = false
    private var currentPassword = ""
    private var changePassword = ""
    private lateinit var binding: MyPageUpdateActivityBinding

    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    private val myPageUpdateViewModel: MyPageUpdateViewModel by viewModels {
        MyPageUpdateViewModelFactory(this@MyPageUpdateActivity)
    }

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                Toast.makeText(
                    this,
                    R.string.public_toast_permission_grant,
                    Toast.LENGTH_SHORT
                ).show()
                galleryLauncher.launch(
                    Intent(Intent.ACTION_PICK).setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                )
            } else {
                Toast.makeText(
                    this,
                    R.string.public_toast_permission_deny,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    binding.myPageUpdateUserImgProfile.setImageURI(uri)
                    Glide.with(this)
                        .load(uri)
                        .error(R.drawable.public_default_wd2_ivory)
                        .fitCenter()
                        .into(binding.myPageUpdateUserImgProfile)
                    myPageUpdateViewModel.setProfileImage(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyPageUpdateActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.ivory_yellow_background)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 안드로이드 11 이상에서만 동작
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 안드로이드 6.0 이상에서만 동작
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } // 안드로이드 6.0 이하는 상태바 아이콘 색상 변경 지원 안함
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        onBackPressedDispatcher.addCallback(this@MyPageUpdateActivity, onBackPressedCallback)

        myPageUpdateClose.setOnClickListener {
            finish()
        }


        // 회원 탈퇴
        txtCancelMember.setOnClickListener {
            showDeleteMemberDialog()
        }


        Glide.with(this@MyPageUpdateActivity)
            .load(profile)
            .error(R.drawable.public_default_wd2_ivory)
            .fitCenter()
            .into(myPageUpdateUserImgProfile)

        editUpdateUserName.setText(name)

        //비밀번호 변경 비활성화
        editUpdateUserCurrentPassword.isEnabled = false
        editUpdateUserPassword.isEnabled = false
        editUpdateUserPasswordCheck.isEnabled = false
        if (passwordJudge) {
            tilUpdateUserCurrentPassword.boxBackgroundColor =
                (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.white))
            tilUpdateUserPassword.boxBackgroundColor =
                (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.white))
            tilUpdateUserPasswordCheck.boxBackgroundColor =
                (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.white))
        } else {
            tilUpdateUserCurrentPassword.boxBackgroundColor =
                (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.light_gray_txt))
            tilUpdateUserPassword.boxBackgroundColor =
                (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.light_gray_txt))
            tilUpdateUserPasswordCheck.boxBackgroundColor =
                (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.light_gray_txt))
        }
        editPassword.setOnClickListener {
            if (passwordJudge) {
                tilUpdateUserCurrentPassword.boxBackgroundColor =
                    (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.light_gray_txt))
                tilUpdateUserPassword.boxBackgroundColor =
                    (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.light_gray_txt))
                tilUpdateUserPasswordCheck.boxBackgroundColor =
                    (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.light_gray_txt))
                editUpdateUserCurrentPassword.isEnabled = false
                editUpdateUserPassword.isEnabled = false
                editUpdateUserPasswordCheck.isEnabled = false
                passwordJudge = false
            } else {
                tilUpdateUserCurrentPassword.boxBackgroundColor =
                    (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.white))
                tilUpdateUserPassword.boxBackgroundColor =
                    (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.white))
                tilUpdateUserPasswordCheck.boxBackgroundColor =
                    (ContextCompat.getColor(this@MyPageUpdateActivity, R.color.white))
                editUpdateUserCurrentPassword.isEnabled = true
                editUpdateUserPassword.isEnabled = true
                editUpdateUserPasswordCheck.isEnabled = true
                passwordJudge = true
            }
        }

        myPageUpdateUserImgProfile.setOnClickListener {
            checkPermissions()
        }
        //이름 변경
        editUpdateUserName.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    myPageUpdateViewModel.checkValidNickname(text.toString().trim())
                }
            })
        }

        btnNameDupCheck.setOnClickListener {
            myPageUpdateViewModel.checkNicknameDuplication(
                editUpdateUserName.text.toString().trim()
            )
        }

        // 비밀번호 형식 확인
        editUpdateUserPassword.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    myPageUpdateViewModel.checkValidPassword(
                        editUpdateUserCurrentPassword.text.toString(),
                        text.toString().trim()
                    )
                }
            })
        }
        // 비밀번호 확인란 edit_text observing
        editUpdateUserPasswordCheck.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    myPageUpdateViewModel.checkValidSamePassword(
                        editUpdateUserPassword.text.toString(),
                        text.toString().trim()
                    )
                }
            })
        }

        btnPasswordDupCheck.setOnClickListener {
            if(passwordJudge) {
                if(editUpdateUserCurrentPassword.text.isNullOrBlank()) {
                    Toast.makeText(this@MyPageUpdateActivity, "현재 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    myPageUpdateViewModel.checkValidCurrentPassword(
                        userInfo.email.toString(),editUpdateUserCurrentPassword.text.toString()
                    )
                }
            }
        }

        //이름은 true false만 해야함
        myPageUpdateBtn.setBtnOnClickListener {
            if (myPageUpdateViewModel.checkAllConditions()) {
                lifecycleScope.launch {
                    name = editUpdateUserName.text.toString().trim()
                    changePassword = editUpdateUserPassword.text.toString().trim()
                    currentPassword = editUpdateUserCurrentPassword.text.toString().trim()
                    profile = myPageUpdateViewModel.ImgProfile.toString()
                    Log.d("check",profile.toString())
                    myPageUpdateViewModel.editInfo(
                        userInfo.id.toString(),
                        profile,
                        name,
                        userInfo.firstLocation.toString(),
                        userInfo.secondLocation.toString(),
                        passwordJudge,
                        userInfo.email,
                        changePassword,
                        currentPassword
                    )
                }
            }
            //비밀번호
            else if (myPageUpdateViewModel.isValidCurrentPassword.value == true
                && myPageUpdateViewModel.isCheckCurrentPassword.value == true
                && myPageUpdateViewModel.isValidPassword.value == true
                && myPageUpdateViewModel.isValidSamePassword.value == true
                && myPageUpdateViewModel.isValidNickname.value == false
                && myPageUpdateViewModel.isValidImg.value != true
            ) {
                changePassword = editUpdateUserPassword.text.toString().trim()
                currentPassword = editUpdateUserCurrentPassword.text.toString().trim()
                myPageUpdateViewModel.editInfo(
                    userInfo.id.toString(),
                    profile,
                    name,
                    userInfo.firstLocation.toString(),
                    userInfo.secondLocation.toString(),
                    passwordJudge,
                    userInfo.email,
                    currentPassword,
                    changePassword,
                )
            }
            //비밀 번호, 이름
            else if (myPageUpdateViewModel.isValidCurrentPassword.value == true
                && myPageUpdateViewModel.isCheckCurrentPassword.value == true
                && myPageUpdateViewModel.isValidPassword.value == true
                && myPageUpdateViewModel.isValidSamePassword.value == true
                && myPageUpdateViewModel.isValidNickname.value == true
                && myPageUpdateViewModel.isNicknameDuplication.value == false
                && myPageUpdateViewModel.isValidImg.value != true
            ) {
                name = editUpdateUserName.text.toString().trim()
                changePassword = editUpdateUserPassword.text.toString().trim()
                currentPassword = editUpdateUserCurrentPassword.text.toString().trim()
                myPageUpdateViewModel.editInfo(
                    userInfo.id.toString(),
                    profile,
                    name,
                    userInfo.firstLocation.toString(),
                    userInfo.secondLocation.toString(),
                    passwordJudge,
                    userInfo.email,
                    currentPassword,
                    changePassword,
                )
            }
            // 비밀번호, 사진
            else if (myPageUpdateViewModel.isValidCurrentPassword.value == true
                && myPageUpdateViewModel.isCheckCurrentPassword.value == true
                && myPageUpdateViewModel.isValidPassword.value == true
                && myPageUpdateViewModel.isValidSamePassword.value == true
                && myPageUpdateViewModel.isValidNickname.value != true
                && myPageUpdateViewModel.isValidImg.value == true
            ) {
                changePassword = editUpdateUserPassword.text.toString().trim()
                currentPassword = editUpdateUserCurrentPassword.text.toString().trim()
                profile = myPageUpdateViewModel.ImgProfile.toString()
                Log.d("check",profile.toString())
                myPageUpdateViewModel.editInfo(
                    userInfo.id.toString(),
                    profile,
                    name,
                    userInfo.firstLocation.toString(),
                    userInfo.secondLocation.toString(),
                    passwordJudge,
                    userInfo.email,
                    currentPassword,
                    changePassword,
                )
            }
            //  이미지, 이름
            else if (myPageUpdateViewModel.isValidCurrentPassword.value != true
                && myPageUpdateViewModel.isCheckCurrentPassword.value != true
                && myPageUpdateViewModel.isValidPassword.value != true
                && myPageUpdateViewModel.isValidSamePassword.value != true
                && myPageUpdateViewModel.isValidNickname.value == true
                && myPageUpdateViewModel.isNicknameDuplication.value == false
                && myPageUpdateViewModel.isValidImg.value == true
            ) {
                name = editUpdateUserName.text.toString().trim()
                profile = myPageUpdateViewModel.ImgProfile.toString()
                myPageUpdateViewModel.editInfo(
                    userInfo.id.toString(),
                    profile,
                    name,
                    userInfo.firstLocation.toString(),
                    userInfo.secondLocation.toString(),
                    passwordJudge,
                    null,
                    null,
                    null,
                )
            }
            //이름
            else if (myPageUpdateViewModel.isValidCurrentPassword.value != true
                && myPageUpdateViewModel.isCheckCurrentPassword.value != true
                && myPageUpdateViewModel.isValidPassword.value != true
                && myPageUpdateViewModel.isValidSamePassword.value != true
                && myPageUpdateViewModel.isValidNickname.value == true
                && myPageUpdateViewModel.isNicknameDuplication.value == false
                && myPageUpdateViewModel.isValidImg.value != true
            ) {
                name = editUpdateUserName.text.toString().trim()
                myPageUpdateViewModel.editInfo(
                    userInfo.id.toString(),
                    profile,
                    name,
                    userInfo.firstLocation.toString(),
                    userInfo.secondLocation.toString(),
                    passwordJudge,
                    null,
                    null,
                    null,
                )
            }
            // 이미지
            else if (myPageUpdateViewModel.isValidCurrentPassword.value != true
                && myPageUpdateViewModel.isCheckCurrentPassword.value != true
                && myPageUpdateViewModel.isValidPassword.value != true
                && myPageUpdateViewModel.isValidSamePassword.value != true
                && myPageUpdateViewModel.isValidNickname.value != true
                && myPageUpdateViewModel.isValidImg.value == true
            ) {
                profile = myPageUpdateViewModel.ImgProfile.toString()

                Log.d("check",profile.toString())

                myPageUpdateViewModel.editInfo(
                    userInfo.id.toString(),
                    profile,
                    editUpdateUserName.text.toString().trim(),
                    userInfo.firstLocation.toString(),
                    userInfo.secondLocation.toString(),
                    passwordJudge,
                    null,
                    null,
                    null,
                )
            } else {
                Toast.makeText(
                    applicationContext,
                    "입력 사항을 다시 한 번 확인해주세요",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initViewModel() = with(binding) {
        myPageUpdateViewModel._isValidNickname.value = false
        myPageUpdateViewModel._isValidImg.value = null
        myPageUpdateViewModel._setResult.value = false

        myPageUpdateViewModel.isValidNickname.observe(this@MyPageUpdateActivity) { isValid ->
            if (isValid) {
                tilUpdateUserName.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.dodger_blue)
                nameDupCheck.isEnabled = true
            } else {
                tilUpdateUserName.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
                nameDupCheck.isEnabled = false
            }
        }

        myPageUpdateViewModel.isNicknameDuplication.observe(this@MyPageUpdateActivity) { isDup ->
            if (!isDup) {
                editUpdateUserName.isEnabled = false
                nameDupCheck.apply {
                    text = "사용 가능"
                    isEnabled = false
                    setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                }
                Toast.makeText(
                    applicationContext,
                    "닉네임 사용 가능",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                nameDupCheck.apply {
                    text = "중복"
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                }
                tilUpdateUserName.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
                Toast.makeText(
                    applicationContext,
                    "닉네임 중복",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        myPageUpdateViewModel.isNicknameDuplication.observe(this@MyPageUpdateActivity) { isDup ->
            if (!isDup) {
                editUpdateUserName.isEnabled = false
                nameDupCheck.apply {
                    text = "사용 가능"
                    isEnabled = false
                    setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                }
            } else {
                nameDupCheck.apply {
                    text = "중복"
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                }
                tilUpdateUserName.boxStrokeColor = ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
            }
        }

        myPageUpdateViewModel.isCheckCurrentPassword.observe(this@MyPageUpdateActivity) { isDup ->
            if (isDup) {
                editUpdateUserCurrentPassword.isEnabled = false
                passwordDupCheck.apply {
                    text = "비밀번호 일치"
                    isEnabled = false
                    setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                }
            } else {
                passwordDupCheck.apply {
                    text = "일치하지 않음"
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                }
                tilUpdateUserName.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
            }
        }

        myPageUpdateViewModel.isCheckCurrentPassword.observe(this@MyPageUpdateActivity){isCheck ->
            if (isCheck == true){
                tilUpdateUserCurrentPassword.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.dodger_blue)
            }
            else{
                tilUpdateUserCurrentPassword.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
            }
        }

        myPageUpdateViewModel.isValidPassword.observe(this@MyPageUpdateActivity) { isValid ->
            if (isValid == true && myPageUpdateViewModel.isValidCurrentPassword.value == true) {
                tilUpdateUserPassword.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.dodger_blue)
                updateUserPasswordJudge.apply {
                    setText(R.string.group_add_txt_password_valid)
                    setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f)
                }
            } else if (myPageUpdateViewModel.isValidCurrentPassword.value == false) {
                tilUpdateUserPassword.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
                updateUserPasswordJudge.apply {
                    setText(R.string.group_add_txt_current_password_invalid)
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.0f)
                }
            } else {
                tilUpdateUserPassword.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
                updateUserPasswordJudge.apply {
                    setText(R.string.group_add_txt_password_invalid)
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.0f)
                }
            }
        }

        myPageUpdateViewModel.isValidSamePassword.observe(this@MyPageUpdateActivity) { isValid ->
            updateUserPasswordCheckJudge.visibility = View.VISIBLE
            if (isValid) {
                tilUpdateUserPasswordCheck.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.dodger_blue)
                updateUserPasswordCheckJudge.apply {
                    setText(R.string.signup_pw_match)
                    setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                }
            } else {
                tilUpdateUserPasswordCheck.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
                updateUserPasswordCheckJudge.apply {
                    setText(R.string.signup_pw_not_match)
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                }
            }
        }

        myPageUpdateViewModel.setResult.observe(this@MyPageUpdateActivity) { result ->
            if (result
                && myPageUpdateViewModel.checkAllConditions()
            ) {
                val intent = Intent().apply {
                    putExtra(
                        MyPageFragment.EXTRA_USER_NAME,
                        name
                    )
                    putExtra(
                        MyPageFragment.EXTRA_USER_PROFILE,
                        profile.toString()
                    )
                }
                setResult(Activity.RESULT_OK, intent)
                Toast.makeText(
                    applicationContext,
                    "수정 성공",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            //비밀번호
            else if (result
                && myPageUpdateViewModel.isValidCurrentPassword.value == true
                && myPageUpdateViewModel.isCheckCurrentPassword.value == true
                && myPageUpdateViewModel.isValidPassword.value == true
                && myPageUpdateViewModel.isValidSamePassword.value == true
                && myPageUpdateViewModel.isValidNickname.value == false
                && myPageUpdateViewModel.isValidImg.value != true
            ) {
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                Toast.makeText(
                    applicationContext,
                    "수정 성공",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            //비밀 번호, 이름
            else if (result
                && myPageUpdateViewModel.isValidCurrentPassword.value == true
                && myPageUpdateViewModel.isCheckCurrentPassword.value == true
                && myPageUpdateViewModel.isValidPassword.value == true
                && myPageUpdateViewModel.isValidSamePassword.value == true
                && myPageUpdateViewModel.isValidNickname.value == true
                && myPageUpdateViewModel.isNicknameDuplication.value == false
                && myPageUpdateViewModel.isValidImg.value != true
            ) {
                val intent = Intent().apply {
                    putExtra(
                        MyPageFragment.EXTRA_USER_NAME,
                        name
                    )
                }
                setResult(Activity.RESULT_OK, intent)
                Toast.makeText(
                    applicationContext,
                    "수정 성공",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            // 비밀번호, 사진
            else if (result
                && myPageUpdateViewModel.isValidCurrentPassword.value == true
                && myPageUpdateViewModel.isCheckCurrentPassword.value == true
                && myPageUpdateViewModel.isValidPassword.value == true
                && myPageUpdateViewModel.isValidSamePassword.value == true
                && myPageUpdateViewModel.isValidNickname.value != true
                && myPageUpdateViewModel.isValidImg.value == true
            ) {
                val intent = Intent().apply {
                    putExtra(
                        MyPageFragment.EXTRA_USER_PROFILE,
                        profile.toString()
                    )
                }
                setResult(Activity.RESULT_OK, intent)
                Toast.makeText(
                    applicationContext,
                    "수정 성공",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            } else if (
            // 이름 , 이미지
                result
                && myPageUpdateViewModel.isValidCurrentPassword.value != true
                && myPageUpdateViewModel.isValidPassword.value != true
                && myPageUpdateViewModel.isValidSamePassword.value != true
                && myPageUpdateViewModel.isValidNickname.value == true
                && myPageUpdateViewModel.isNicknameDuplication.value == false
                && myPageUpdateViewModel.isValidImg.value == true
            ) {
                val intent = Intent().apply {
                    putExtra(
                        MyPageFragment.EXTRA_USER_NAME,
                        name
                    )
                    putExtra(
                        MyPageFragment.EXTRA_USER_PROFILE,
                        profile.toString()
                    )
                }
                setResult(Activity.RESULT_OK, intent)
                Toast.makeText(
                    applicationContext,
                    "수정 성공",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            } else if (
            // 이름
                result
                && myPageUpdateViewModel.isValidCurrentPassword.value != true
                && myPageUpdateViewModel.isValidPassword.value != true
                && myPageUpdateViewModel.isValidSamePassword.value != true
                && myPageUpdateViewModel.isValidNickname.value == true
                && myPageUpdateViewModel.isNicknameDuplication.value == false
                && myPageUpdateViewModel.isValidImg.value != true
            ) {
                val intent = Intent().apply {
                    putExtra(
                        MyPageFragment.EXTRA_USER_NAME,
                        name
                    )
                }
                setResult(Activity.RESULT_OK, intent)
                Toast.makeText(
                    applicationContext,
                    "수정 성공",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            } else if (
            // 이미지
                result
                && myPageUpdateViewModel.isValidCurrentPassword.value != true
                && myPageUpdateViewModel.isValidPassword.value != true
                && myPageUpdateViewModel.isValidSamePassword.value != true
                && myPageUpdateViewModel.isValidNickname.value == false
                && myPageUpdateViewModel.isValidImg.value == true
            ) {

                val intent = Intent().apply {
                    putExtra(
                        MyPageFragment.EXTRA_USER_PROFILE,
                        profile.toString()
                    )
                }
                setResult(Activity.RESULT_OK, intent)
                Toast.makeText(
                    applicationContext,
                    "수정 성공",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }
    }

    private fun showDeleteMemberDialog() {
        AlertDialog.Builder(this@MyPageUpdateActivity).apply {
            setTitle(R.string.my_page_dialog_delete_member_title)
            setMessage(R.string.my_page_dialog_delete_member_message)
            setPositiveButton(R.string.public_dialog_ok) { _, _ ->
                myPageUpdateViewModel.deleteMember(userInfo.id)
                startActivity(
                    Intent(context, SignInActivity::class.java)
                )
                ActivityCompat.finishAffinity(this@MyPageUpdateActivity)
            }
            show()
        }
    }

    private fun checkPermissions() {
        when {
            permissions.all {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            } -> {
                galleryLauncher.launch(
                    Intent(Intent.ACTION_PICK).setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                )
            }

            permissions.any {
                shouldShowRequestPermissionRationale(it)
            } -> { //이전에 권한 요청을 거부한 적이 있는 경우
                showRationalDialog()
            }

            else -> galleryPermissionLauncher.launch(permissions)
        }
    }

    private fun showRationalDialog() {
        AlertDialog.Builder(this@MyPageUpdateActivity).apply {
            setTitle(R.string.public_dialog_rational_title)
            setMessage(R.string.public_dialog_rational_message)
            setPositiveButton(R.string.public_dialog_ok) { _, _ ->
                galleryPermissionLauncher.launch(permissions)
            }
            show()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}