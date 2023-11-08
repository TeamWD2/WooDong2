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
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.wd.woodong2.R
import com.wd.woodong2.databinding.MyPageUpdateActivityBinding
import com.wd.woodong2.presentation.chat.content.UserItem
import com.wd.woodong2.presentation.mypage.content.MyPageFragment
import kotlinx.coroutines.launch


class MyPageUpdateActivity : AppCompatActivity(){

    companion object {
        lateinit var userInfo: UserItem

        fun newIntent(context: Context, userItem: UserItem)=//MutableLiveData<UserItem>)=
            Intent(context, MyPageUpdateActivity::class.java).apply {
                userInfo = userItem
            }

    }

    private var profile = userInfo.imgProfile
    private var name = userInfo.name
    private var passwordJudge = false
    private var currentPassword = ""
    private var changePassword = ""
    private lateinit var binding : MyPageUpdateActivityBinding

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
    private val onBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    private val myPageUpdateViewModel: MyPageUpdateViewModel by viewModels {
        MyPageUpdateViewModelFactory()
    }

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                initView()
            } else {
                Toast.makeText(this, getString(R.string.main_toast_permission), Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                profile = result.data?.data.toString()

                Glide.with(this)
                    .load(Uri.parse(profile))
                    .error(R.drawable.group_ic_no_image)
                    .fitCenter()
                    .into(binding.myPageUpdateUserImgProfile)

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyPageUpdateActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.wd2_main_color)

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

        myPageUpdateClose.setOnClickListener{
            finish()
        }

        //기본설정
        Glide.with(this@MyPageUpdateActivity)
            .load(profile)
            .error(R.drawable.group_ic_no_image)
            .fitCenter()
            .into(myPageUpdateUserImgProfile)

        editUpdateUserName.setText(name)

        //비밀번호 변경 비활성화
        editUpdateUserCurrentPassword.isEnabled = false
        editUpdateUserPassword.isEnabled = false
        editUpdateUserPasswordCheck.isEnabled = false
        if(passwordJudge){
            editUpdateUserCurrentPassword.setBackgroundResource(R.drawable.my_page_update_et_shape)
            editUpdateUserPassword.setBackgroundResource(R.drawable.my_page_update_et_shape)
            editUpdateUserPasswordCheck.setBackgroundResource(R.drawable.my_page_update_et_shape)
        }
        else{
            editUpdateUserCurrentPassword.setBackgroundResource(R.drawable.my_page_update_et_shape_not)
            editUpdateUserPassword.setBackgroundResource(R.drawable.my_page_update_et_shape_not)
            editUpdateUserPasswordCheck.setBackgroundResource(R.drawable.my_page_update_et_shape_not)

        }
        editPassword.setOnClickListener {
            if(passwordJudge){
                editUpdateUserCurrentPassword.setBackgroundResource(R.drawable.my_page_update_et_shape_not)
                editUpdateUserPassword.setBackgroundResource(R.drawable.my_page_update_et_shape_not)
                editUpdateUserPasswordCheck.setBackgroundResource(R.drawable.my_page_update_et_shape_not)

            editUpdateUserCurrentPassword.isEnabled = false
            editUpdateUserPassword.isEnabled = false
            editUpdateUserPasswordCheck.isEnabled = false
            passwordJudge = false
            }
            else{
                editUpdateUserCurrentPassword.setBackgroundResource(R.drawable.my_page_update_et_shape)
                editUpdateUserPassword.setBackgroundResource(R.drawable.my_page_update_et_shape)
                editUpdateUserPasswordCheck.setBackgroundResource(R.drawable.my_page_update_et_shape)


                editUpdateUserCurrentPassword.isEnabled = true
                editUpdateUserPassword.isEnabled = true
                editUpdateUserPasswordCheck.isEnabled = true
                passwordJudge = true
            }
        }

        myPageUpdateUserImgProfile.setOnClickListener{
            checkPermissions()
        }

        editUpdateUserName.apply{
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    myPageUpdateViewModel.checkValidNickname(text.toString().trim())
                    nameDupCheck.apply {
                        text = "중복 체크"
                        setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                    }
                }
            })
        }

        nameDupCheck.setOnClickListener {
            myPageUpdateViewModel.checkNicknameDuplication(editUpdateUserName.text.toString().trim())
        }

        // 비밀번호 형식 확인
        editUpdateUserPassword.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    myPageUpdateViewModel.checkValidPassword(
                        editUpdateUserCurrentPassword.text.toString(),
                        text.toString().trim())
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
        myPageUpdateBtn.setOnClickListener{
            if(myPageUpdateViewModel.checkAllConditions()){
                lifecycleScope.launch {

                    name = editUpdateUserName.text.toString().trim()
                    changePassword = editUpdateUserPassword.text.toString().trim()
                    currentPassword = editUpdateUserCurrentPassword.text.toString().trim()

                    myPageUpdateViewModel.editInfo(
                        userInfo.id.toString(),
                        userInfo.email.toString(),
                        editUpdateUserCurrentPassword.text.toString().trim(),
                        editUpdateUserPassword.text.toString().trim(),
                        profile.toString(),
                        editUpdateUserName.text.toString().trim(),
                        userInfo.firstLocation.toString(),
                        userInfo.secondLocation.toString(),
                        passwordJudge
                    )
                }

            }else {
                Toast.makeText(
                    applicationContext,
                    "입력 사항을 다시 한 번 확인해주세요",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }




    }
    private fun initViewModel() = with(binding) {
        myPageUpdateViewModel.isValidNickname.observe(this@MyPageUpdateActivity){isValid ->
            if (isValid) {
                tilUpdateUserName.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.dodger_blue)
                nameDupCheck.isEnabled = true
            } else {
                tilUpdateUserName.boxStrokeColor = ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
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


        myPageUpdateViewModel.isValidPassword.observe(this@MyPageUpdateActivity) { isValid ->
            if (isValid && myPageUpdateViewModel.isValidCurrentPassword.value == true) {
                tilUpdateUserPassword.boxStrokeColor =
                    ContextCompat.getColor(this@MyPageUpdateActivity, R.color.dodger_blue)
                updateUserPasswordJudge.apply {
                    setText(R.string.group_add_txt_password_valid)
                    setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f)
                }
            }
            else if(myPageUpdateViewModel.isValidCurrentPassword.value == false){
                tilUpdateUserPassword.boxStrokeColor = ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
                updateUserPasswordJudge.apply {
                    setText(R.string.group_add_txt_current_password_invalid)
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.0f)
                }
            }
            else {
                tilUpdateUserPassword.boxStrokeColor = ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
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
                tilUpdateUserPasswordCheck.boxStrokeColor = ContextCompat.getColor(this@MyPageUpdateActivity, R.color.red)
                updateUserPasswordCheckJudge.apply {
                    setText(R.string.signup_pw_not_match)
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                }
            }
        }

        myPageUpdateViewModel.setResult.observe(this@MyPageUpdateActivity){result ->
            when (result) {
                true -> {
                    Log.d("mypage3", name!!)
                    val intent = Intent().apply {
                        putExtra(
                            MyPageFragment.EXTRA_USER_NAME,
                            name
                        )
                        putExtra(
                            MyPageFragment.EXTRA_USER_PROFILE,
                            profile
                        )
                        if(passwordJudge){
                            putExtra(
                                MyPageFragment.EXTRA_USER_CURRENT_PASSWORD,
                                currentPassword
                            )
                            putExtra(
                                MyPageFragment.EXTRA_USER_PASSWORD,
                                changePassword
                            )
                        }
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
    }
    private fun checkPermissions() {
        if (permissions.all {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            galleryLauncher.launch(
                Intent(Intent.ACTION_PICK).setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
            )
        } else {
            galleryPermissionLauncher.launch(permissions)
        }
    }
}