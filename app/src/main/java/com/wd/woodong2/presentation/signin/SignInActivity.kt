package com.wd.woodong2.presentation.signin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.R
import com.wd.woodong2.databinding.SigninActivityBinding
import com.wd.woodong2.presentation.main.MainActivity
import com.wd.woodong2.presentation.signup.SignUpActivity

class SignInActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SignInActivity"
    }

    private var _binding: SigninActivityBinding? = null
    private val binding get() = _binding!!

    private val signInViewModel: SignInViewModel by viewModels {
        SignInViewModelFactory(
            this
        )
    }

    private val signUpActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.editId.setText(result.data?.getStringExtra(SignUpActivity.SIGN_UP_ID) ?: "")
            binding.editPassword.setText(
                result.data?.getStringExtra(SignUpActivity.SIGN_UP_PW) ?: ""
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = SigninActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initInfo()
        initView()
        initModel()
    }

    private fun initInfo() {
        val uid = signInViewModel.isAutoLogin()

        if (uid != null) {
            startActivity(MainActivity.newIntentForAutoLogin(this@SignInActivity, uid))
            signInViewModel.setUserInfo(uid)
            signInViewModel.updateToken(uid)
            finish()
        }
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

        // 회원 가입 버튼 클릭 시
        txtSignup.setOnClickListener {
            signUpActivityResultLauncher.launch(
                Intent(this@SignInActivity, SignUpActivity::class.java)
            )
        }

        // 로그인 버튼 클릭 시
        btnLogin.setOnClickListener {
            signInViewModel.signIn(
                editId.text.toString(),
                editPassword.text.toString(),
                checkboxAutoLogin.isChecked
            )
        }
    }

    private fun initModel() = with(signInViewModel) {
        loginResult.observe(this@SignInActivity) { result ->
            val uid = getUserUIDFromAuth()

            if (result && uid != null) {
                signInViewModel.setUserInfo(uid)
            } else {
                Toast.makeText(this@SignInActivity, R.string.login_fail, Toast.LENGTH_SHORT).show()
            }
        }

        isSetUserInfo.observe(this@SignInActivity) { isSetUserInfo ->
            if (isSetUserInfo) {
                val uid = getUserUIDFromAuth()

                // TODO
                startActivity(
                    MainActivity.newIntentForLogin(
                        this@SignInActivity,
                        uid ?: ""
                    )
                )

                Toast.makeText(this@SignInActivity, R.string.login_success, Toast.LENGTH_SHORT)
                    .show()

                finish()
            } else {
                Toast.makeText(this@SignInActivity, R.string.signin_login_retry, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
