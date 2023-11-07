package com.wd.woodong2.presentation.start

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.databinding.StartActivityBinding
import com.wd.woodong2.presentation.main.MainActivity
import com.wd.woodong2.presentation.signin.SignInActivity

class StartActivity : AppCompatActivity() {
    private var _binding: StartActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = StartActivityBinding.inflate(layoutInflater)
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

        // start_btn click
        btnStart.setOnClickListener {
            startActivity(
                Intent(this@StartActivity, MainActivity::class.java)
            )
        }
    }

    private fun initModel() {

    }
}