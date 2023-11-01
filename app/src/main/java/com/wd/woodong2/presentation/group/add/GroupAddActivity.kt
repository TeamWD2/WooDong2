package com.wd.woodong2.presentation.group.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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