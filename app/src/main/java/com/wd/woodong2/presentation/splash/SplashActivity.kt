package com.wd.woodong2.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.databinding.SplashActivityBinding
import com.wd.woodong2.presentation.signin.SignInActivity

class SplashActivity : AppCompatActivity() {

    private var _binding: SplashActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }, SPLASH_DISPLAY_LENGTH)
    }

    companion object {
        const val SPLASH_DISPLAY_LENGTH = 3000L
    }
}
