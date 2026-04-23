package com.wd.woodong2.presentation.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.wd.woodong2.R
import com.wd.woodong2.databinding.MainActivityBinding
import com.wd.woodong2.presentation.chat.content.ChatFragment
import com.wd.woodong2.presentation.group.content.GroupFragment
import com.wd.woodong2.presentation.home.content.HomeFragment
import com.wd.woodong2.presentation.mypage.content.MyPageFragment

class MainActivity : AppCompatActivity() {

    companion object {
        private const val UID = "UID"
        fun newIntentForLogin(context: Context, uid: String): Intent =
            Intent(context, MainActivity::class.java).apply {
                putExtra(UID, uid)
            }

        fun newIntentForAutoLogin(context: Context, uid: String): Intent =
            Intent(context, MainActivity::class.java).apply {
                putExtra(UID, uid)
            }
    }

    private lateinit var binding: MainActivityBinding

    private val notificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Toast.makeText(
                this,
                if (granted) R.string.public_toast_noti_permission_grant else R.string.public_toast_noti_permission_deny,
                Toast.LENGTH_SHORT
            ).show()

            initView()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        checkPermissions()

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun checkPermissions() {
        if (notificationPermission == null) {
            initView()
            return
        }

        val granted = ContextCompat.checkSelfPermission(
            this,
            notificationPermission
        ) == PackageManager.PERMISSION_GRANTED

        when {
            granted -> initView()

            shouldShowRequestPermissionRationale(notificationPermission) -> {
                showRationalDialog()
            }

            else -> permissionLauncher.launch(notificationPermission)
        }
    }

    private fun initView() = with(binding) {
        //BottomNavigation 설정
        bottomNavigation.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.bottom_menu_home -> HomeFragment()
                R.id.bottom_menu_group -> GroupFragment()
                R.id.bottom_menu_chat -> ChatFragment()
                else -> MyPageFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(frameLayout.id, selectedFragment).commit()
            true
        }
        supportFragmentManager.beginTransaction()
            .replace(frameLayout.id, HomeFragment()).commit()
    }

    private fun showRationalDialog() {
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle(R.string.public_dialog_rational_title)
            setMessage(R.string.public_dialog_rational_message)
            setPositiveButton(R.string.public_dialog_ok) { _, _ ->
                notificationPermission?.let {
                    permissionLauncher.launch(it)
                }
            }
            show()
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.exit_confirmation_title))
            setMessage(getString(R.string.exit_confirmation_message))
            setPositiveButton(getString(R.string.exit_confirmation_positive_button)) { _, _ ->
                finish() // 앱 종료
            }
            setNegativeButton(getString(R.string.exit_confirmation_negative_button), null)
            show()
        }
    }
}