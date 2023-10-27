package com.wd.woodong2.presentation.signin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.databinding.SigninActivityBinding
import com.wd.woodong2.presentation.main.MainActivity
import com.wd.woodong2.presentation.signup.SignUpActivity

class SignInActivity : AppCompatActivity() {

    private var _binding: SigninActivityBinding? = null
    private val binding get() = _binding!!

    private val signInViewModel: SignInViewModel by viewModels {
        SignInViewModelFactory()
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

        //signup btn click
        btnSignup.setOnClickListener {
            startActivity(
                Intent(this@SignInActivity, SignUpActivity::class.java)
            )
        }

        // 로그인 버튼 클릭 시
        btnLogin.setOnClickListener {
            signInViewModel.signIn(editId.text.toString(), editPassword.text.toString())
        }
    }

    private fun initModel() = with(signInViewModel) {
        loginResult.observe(this@SignInActivity) { result ->
            if (result) {
                Toast.makeText(this@SignInActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                startActivity(MainActivity.newIntentForMain(this@SignInActivity))
            } else {
                Toast.makeText(this@SignInActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}