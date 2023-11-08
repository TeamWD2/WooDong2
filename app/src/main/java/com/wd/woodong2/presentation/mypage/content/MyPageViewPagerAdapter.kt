package com.wd.woodong2.presentation.mypage.content

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wd.woodong2.R
import com.wd.woodong2.presentation.mypage.content.group.MyPageGroupFragment
import com.wd.woodong2.presentation.mypage.content.thumb.MyPageThumbFragment
import com.wd.woodong2.presentation.mypage.content.written.MyPageWrittenFragment

class MyPageViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val size = 3

    fun getTitle(position: Int): Int {
        return when (position) {
            0 -> R.string.my_page_tab_tv_written
            1 -> R.string.my_page_tab_tv_group
            2 -> R.string.my_page_tab_tv_thumb
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyPageWrittenFragment()
            1 -> MyPageGroupFragment()
            2 -> MyPageThumbFragment()
            else -> throw IllegalStateException()
        }
    }
}