package com.wd.woodong2.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.wd.woodong2.R
import com.wd.woodong2.databinding.MainActivityBinding
import com.wd.woodong2.presentation.chat.content.ChatFragment
import com.wd.woodong2.presentation.group.content.GroupFragment
import com.wd.woodong2.presentation.home.content.HomeFragment
import com.wd.woodong2.presentation.home.map.HomeMapActivity
import com.wd.woodong2.presentation.mypage.content.MyPageFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : MainActivityBinding

    private val homeFragment by lazy {
        HomeFragment.newInstance()
    }

    private val groupFragment by lazy {
        GroupFragment.newInstance()
    }

    private val chatFragment by lazy {
        ChatFragment.newInstance()
    }

    private val myPageFragment by lazy {
        MyPageFragment.newInstance()
    }
    private val homeMapLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if(result.resultCode == Acitivty.RESULT_OK){
//
//            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }
    private fun initView() = with(binding) {

        // Toolbar 설정
        setSupportActionBar(mainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mainToolbarLocation.setOnClickListener{
            homeMapLauncher.launch(
                HomeMapActivity.newIntent(
                    this@MainActivity
                )
            )
        }

        //BottomNavigation 설정
        supportFragmentManager.beginTransaction().add(frameLayout.id, homeFragment).commit()
        bottomNavigation.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.bottom_menu_home -> homeFragment
                R.id.bottom_menu_group -> groupFragment
                R.id.bottom_menu_chat -> chatFragment
                else -> myPageFragment
            }
            if (!selectedFragment.isAdded) {
                supportFragmentManager.beginTransaction()
                    .replace(frameLayout.id, selectedFragment).commit()
            }
            true
        }

        // fabAddTodo 버튼에 대한 코드


    }
}