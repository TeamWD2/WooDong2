package com.wd.woodong2.presentation.signup

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.wd.woodong2.R
import com.wd.woodong2.databinding.SignupActivityBinding
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    companion object {
        const val SIGN_UP_ID = "sign_up_id"
        const val SIGN_UP_PW = "sign_up_pw"
    }

    private var _binding: SignupActivityBinding? = null
    private val binding get() = _binding!!

    /**
     * 갤러리 접근 권한 설정
     * Target SDK 33 부터 READ_EXTERNAL_STORAGE 권한 세분화 (이미지/동영상/오디오)
     * Android 13(VERSION_CODES.TIRAMISU) 버전 체크하여 권한 요청 필요
     */
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
                result.data?.data?.let { uri ->
                    binding.ivProfile.setImageURI(uri)
                    signViewModel.setProfileImage(uri)
                }
            }
        }

    private val signViewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory()
    }

    lateinit var id: String
    lateinit var pw: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = SignupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initInfo()
        initView()
        initModel()
    }

    private fun initInfo() {

    }

    private fun initView() = with(binding) {
        // 상태바 감추기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        // 프로필 버튼 클릭 시
        ivProfile.setOnClickListener {
            checkPermissions()
        }

        // id 형식 확인
        editId.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    txtCheckIdDuplication.visibility = View.INVISIBLE
                }
            })
        }

        // 비밀번호 형식 확인
        editPw.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    signViewModel.checkValidPassword(text.toString().trim())
                }
            })
        }

        editId.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    signViewModel.checkValidId(text.toString().trim())
                }
            })
        }

        // 비밀번호 확인란 edit_text observing
        editPwCheck.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    signViewModel.checkValidSamePassword(
                        editPw.text.toString(),
                        text.toString().trim()
                    )
                }
            })
        }

        editName.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {
                    signViewModel.checkValidNickname(text.toString().trim())
                    txtCheckNicknameDuplication.apply {
                        text = "중복 체크"
                        setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                    }
                }
            })
        }

        txtCheckNicknameDuplication.setOnClickListener {
            signViewModel.checkNicknameDuplication(editName.text.toString().trim())
        }

        btnSummit.setOnClickListener {
            if (signViewModel.checkAllConditions()) {
                lifecycleScope.launch {
                    signViewModel.signUp(
                        editId.text.toString().trim(),
                        editPw.text.toString().trim(),
                        editName.text.toString().trim()
                    )
                }
                id = editId.text.toString().trim()
                pw = editPw.text.toString().trim()
            } else {
                Toast.makeText(
                    applicationContext,
                    "입력 사항을 다시 한 번 확인해주세요",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initModel() = with(binding) {
        signViewModel.isValidId.observe(this@SignUpActivity) { isValid ->
            if (isValid) {
                tilId.boxStrokeColor =
                    ContextCompat.getColor(this@SignUpActivity, R.color.dodger_blue)
                txtCheckIdDuplication.isClickable = true
            } else {
                tilId.boxStrokeColor =
                    ContextCompat.getColor(this@SignUpActivity, R.color.red)
                txtCheckIdDuplication.isClickable = false
            }
        }

        signViewModel.isValidPassword.observe(this@SignUpActivity) { isValid ->
            if (isValid) {
                tilPw.boxStrokeColor =
                    ContextCompat.getColor(this@SignUpActivity, R.color.dodger_blue)
                txtCheckCorrectPw.apply {
                    setText(R.string.group_add_txt_password_valid)
                    setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12.0f)
                }
            } else {
                tilPw.boxStrokeColor = ContextCompat.getColor(this@SignUpActivity, R.color.red)
                txtCheckCorrectPw.apply {
                    setText(R.string.group_add_txt_password_invalid)
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10.0f)
                }
            }
        }

        signViewModel.isValidSamePassword.observe(this@SignUpActivity) { isValid ->
            txtCheckPwNotification.visibility = View.VISIBLE
            if (isValid) {
                tilPwCheck.boxStrokeColor =
                    ContextCompat.getColor(this@SignUpActivity, R.color.dodger_blue)
                txtCheckPwNotification.apply {
                    setText(R.string.signup_pw_match)
                    setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                }
            } else {
                tilPwCheck.boxStrokeColor = ContextCompat.getColor(this@SignUpActivity, R.color.red)
                txtCheckPwNotification.apply {
                    setText(R.string.signup_pw_not_match)
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                }
            }
        }

        signViewModel.isValidNickname.observe(this@SignUpActivity) { isValid ->
            if (isValid) {
                tilName.boxStrokeColor =
                    ContextCompat.getColor(this@SignUpActivity, R.color.dodger_blue)
                txtCheckNicknameDuplication.isEnabled = true
            } else {
                tilName.boxStrokeColor = ContextCompat.getColor(this@SignUpActivity, R.color.red)
                txtCheckNicknameDuplication.isEnabled = false
            }
        }

        /*
        * 닉네임 중복 체크 메소드
        */
        signViewModel.isNicknameDuplication.observe(this@SignUpActivity) { isDup ->
            if (!isDup) {
                editName.isEnabled = false
                txtCheckNicknameDuplication.apply {
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
                txtCheckNicknameDuplication.apply {
                    text = "중복"
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                }
                tilName.boxStrokeColor =
                    ContextCompat.getColor(this@SignUpActivity, R.color.red)
                Toast.makeText(
                    applicationContext,
                    "닉네임 중복",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        signViewModel.signUpResult.observe(this@SignUpActivity) { result ->
            when (result) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                    txtCheckIdDuplication.visibility = View.VISIBLE
                    tilId.boxStrokeColor =
                        ContextCompat.getColor(this@SignUpActivity, R.color.red)

                    Toast.makeText(
                        applicationContext,
                        "ID 중복",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                true -> {
                    val intent = Intent().apply {
                        putExtra(
                            SIGN_UP_ID,
                            id
                        )
                        putExtra(
                            SIGN_UP_PW,
                            pw
                        )
                    }
                    setResult(Activity.RESULT_OK, intent)

                    Toast.makeText(
                        applicationContext,
                        "회원 가입 성공",
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