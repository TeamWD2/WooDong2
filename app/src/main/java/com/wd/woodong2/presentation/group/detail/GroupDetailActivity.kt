package com.wd.woodong2.presentation.group.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wd.woodong2.databinding.GroupDetailActivityBinding

class GroupDetailActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, GroupDetailActivity::class.java)
    }

    private lateinit var binding: GroupDetailActivityBinding

    private val viewPager2Adapter by lazy {
        GroupDetailViewPagerAdapter(this@GroupDetailActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {
        //Toolbar init
        setSupportActionBar(materialToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        materialToolbar.title = "Test Title"

        //ViewPager2Adapter init
        viewPager2.isUserInputEnabled = false //swipe
        viewPager2.adapter = viewPager2Adapter
        viewPager2.offscreenPageLimit = viewPager2Adapter.itemCount

        //TabLayout X ViewPager2
        TabLayoutMediator(tabLayout, viewPager2) { tab, pos ->
            tab.setText(viewPager2Adapter.getTitle(pos))
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager2.setCurrentItem(tab.position, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}