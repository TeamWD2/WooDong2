package com.wd.woodong2.presentation.group.add

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.wd.woodong2.databinding.GroupAddActivityBinding

class GroupAddActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, GroupAddActivity::class.java)
    }

    private lateinit var binding: GroupAddActivityBinding

    private val viewModel: GroupAddSharedViewModel by viewModels {
        GroupAddSharedViewModelFactory(this@GroupAddActivity)
    }

    private val viewPager2Adapter by lazy {
        GroupAddViewPagerAdapter(this@GroupAddActivity)
    }

    private val onBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishViewPager2()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        onBackPressedDispatcher.addCallback(this@GroupAddActivity, onBackPressedCallback)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        viewPager2GroupAdd.apply {
            adapter = viewPager2Adapter
            offscreenPageLimit = viewPager2Adapter.itemCount
        }
        dotsIndicator.attachTo(viewPager2GroupAdd)

        imgBack.setOnClickListener {
            finishViewPager2()
        }
    }

    private fun initViewModel() = with(viewModel) {
        viewModel.viewPager2CurItem.observe(this@GroupAddActivity) { curItem ->
            binding.viewPager2GroupAdd.setCurrentItem(curItem, true)
        }
    }

    private fun finishViewPager2() {
        if(binding.viewPager2GroupAdd.currentItem > 0) {
            viewModel.modifyViewPager2(-1)
        } else {
            finish()
        }
    }

    // 화면 터치 시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }
}