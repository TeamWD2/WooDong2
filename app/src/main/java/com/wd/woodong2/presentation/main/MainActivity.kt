package com.wd.woodong2.presentation.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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

    /**
     * 갤러리 접근 권한 설정
     * Target SDK 33 부터 READ_EXTERNAL_STORAGE 권한 세분화 (이미지/동영상/오디오)
     * Android 13(VERSION_CODES.TIRAMISU) 버전 체크하여 권한 요청 필요
     */
    private val galleryPermissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
            )
        } else {
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

    private val notificationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            Toast.makeText(
                this,
                if (galleryPermissions.all { permissions[it] == true }) R.string.public_toast_gallery_permission_grant else R.string.public_toast_gallery_permission_deny,
                Toast.LENGTH_SHORT
            ).show()

            Toast.makeText(
                this,
                if (permissions[notificationPermission] == true) R.string.public_toast_noti_permission_grant else R.string.public_toast_noti_permission_deny,
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
        val allPermission = if(notificationPermission != null) {
            galleryPermissions + notificationPermission
        } else {
            galleryPermissions
        }

        val galleryGranted = galleryPermissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
        val notificationGranted = if(notificationPermission != null) {
            ContextCompat.checkSelfPermission(
                this,
                notificationPermission
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        when {
            galleryGranted && notificationGranted -> {
                initView()
            }

            allPermission.any {
                shouldShowRequestPermissionRationale(it)
            } -> { //이전에 권한 요청을 거부한 적이 있는 경우
                showRationalDialog(allPermission)
            }

            else -> permissionLauncher.launch(allPermission)
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

    private fun showRationalDialog(allPermission: Array<String>) {
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle(R.string.public_dialog_rational_title)
            setMessage(R.string.public_dialog_rational_message)
            setPositiveButton(R.string.public_dialog_ok) { _, _ ->
                permissionLauncher.launch(allPermission)
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