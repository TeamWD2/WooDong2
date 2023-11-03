package com.wd.woodong2.presentation.group.add

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAddActivityBinding
import com.wd.woodong2.presentation.group.detail.GroupDetailViewPagerAdapter

class GroupAddActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, GroupAddActivity::class.java)
    }

    private lateinit var binding: GroupAddActivityBinding

    private val viewPager2Adapter by lazy {
        GroupAddViewPagerAdapter(this@GroupAddActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {
        //상태바 & 아이콘 색상 변경
        window.statusBarColor = ContextCompat.getColor(this@GroupAddActivity, R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 안드로이드 11 이상에서만 동작
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 안드로이드 6.0 이상에서만 동작
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } // 안드로이드 6.0 이하는 상태바 아이콘 색상 변경 지원 안함

        viewPager2GroupAdd.apply {
            adapter = viewPager2Adapter
            offscreenPageLimit = viewPager2Adapter.itemCount
        }
        dotsIndicator.attachTo(viewPager2GroupAdd)

        imgBack.setOnClickListener {
            finish()
        }
    }
}