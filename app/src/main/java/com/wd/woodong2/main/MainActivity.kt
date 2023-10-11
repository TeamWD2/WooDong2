package com.wd.woodong2.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wd.woodong2.R
import com.wd.woodong2.chat.ChatFragment
import com.wd.woodong2.databinding.MainActivityBinding
import com.wd.woodong2.group.GroupFragment
import com.wd.woodong2.home.HomeFragment
import com.wd.woodong2.mypage.MyPageFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding : MainActivityBinding

    private val frameLayout: FrameLayout by lazy {
        binding.frameLayout
    }

    private val bottomNavigation: BottomNavigationView by lazy {
        binding.bottomNavigation
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }
    private fun initView() = with(binding) {
        supportFragmentManager.beginTransaction().add(frameLayout.id, HomeFragment()).commit()

        bottomNavigation.setOnNavigationItemSelectedListener {
            replaceFragment(
                when (it.itemId) {
                    R.id.bottom_menu_home -> HomeFragment()
                    R.id.bottom_menu_life -> GroupFragment()
                    R.id.bottom_menu_chat -> ChatFragment()
                    else -> MyPageFragment()
                }
            )
            true
        }

        // fabAddTodo 버튼에 대한 코드
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(frameLayout.id, fragment).commit()
    }
}