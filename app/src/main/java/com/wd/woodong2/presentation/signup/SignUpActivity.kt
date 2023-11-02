package com.wd.woodong2.presentation.signup

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.wd.woodong2.R
import com.wd.woodong2.databinding.SignupActivityBinding
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private var _binding: SignupActivityBinding? = null
    private val binding get() = _binding!!

    private val signViewModel: SignUpViewModel by viewModels {
        SignUpViewModelFactory()
    }

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

        // 중복체크 텍스트 클릭 시
        txtCheckIdDuplication.setOnClickListener {
            val id = editId.text.toString()
            signViewModel.idCheckComplete(id)
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
                }
            })
        }

        btnSummit.setOnClickListener {
            signViewModel.isAllCorrect.observe(this@SignUpActivity) { isAllCorrect ->
                if (isAllCorrect) {
                    lifecycleScope.launch {
                        signViewModel.signUp(
                            editId.text.toString().trim(),
                            editPw.text.toString().trim(),
                            editName.text.toString().trim()
                        )
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "모든 항목이 유효하지 않습니다. 다시 확인해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


    private fun initModel() = with(binding) {
        signViewModel.isDuplication.observe(this@SignUpActivity) { isDuplication ->
            if (!isDuplication) {
                Toast.makeText(this@SignUpActivity, "사용 가능한 ID", Toast.LENGTH_SHORT)
                    .show()
                binding.tilId.boxStrokeColor =
                    ContextCompat.getColor(this@SignUpActivity, R.color.dodger_blue)
                binding.editId.isEnabled = false
                binding.tilId.defaultHintTextColor =
                    ContextCompat.getColorStateList(
                        this@SignUpActivity,
                        R.color.gray_light
                    )
            } else {
                Toast.makeText(
                    this@SignUpActivity,
                    "중복된 ID가 존재합니다",
                    Toast.LENGTH_SHORT
                ).show()
                binding.tilId.boxStrokeColor =
                    ContextCompat.getColor(this@SignUpActivity, R.color.red)
            }
        }

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
                    setText(R.string.group_add_password_valid)
                    setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                }
            } else {
                tilPw.boxStrokeColor = ContextCompat.getColor(this@SignUpActivity, R.color.red)
                txtCheckCorrectPw.apply {
                    setText(R.string.group_add_password_invalid)
                    setTextColor(ContextCompat.getColor(context, R.color.red))
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
                txtCheckCorrectName.apply {
                    setText(R.string.nickname_valid)
                    setTextColor(ContextCompat.getColor(context, R.color.dodger_blue))
                }
            } else {
                tilName.boxStrokeColor = ContextCompat.getColor(this@SignUpActivity, R.color.red)
                txtCheckCorrectName.apply {
                    setText(R.string.nickname_invalid)
                    setTextColor(ContextCompat.getColor(context, R.color.red))
                }
            }
        }
    }
}