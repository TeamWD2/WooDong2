package com.wd.woodong2.presentation.mypage.content

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wd.woodong2.R
import com.wd.woodong2.presentation.mypage.content.group.MyPageGroupFragment
import com.wd.woodong2.presentation.mypage.content.thumb.MyPageThumbFragment
import com.wd.woodong2.presentation.mypage.content.written.MyPageWrittenFragment

class MyPageViewPagerAdapter(
    myPageFragment: MyPageFragment
): FragmentStateAdapter(myPageFragment){

    private val fragments = listOf(
        MyPageTabs(MyPageWrittenFragment.newInstance(), R.string.my_page_tab_tv_written),
        MyPageTabs(MyPageGroupFragment.newInstance(), R.string.my_page_tab_tv_group),
        MyPageTabs(MyPageThumbFragment.newInstance(), R.string.my_page_tab_tv_thumb),
    )

    fun getFragment(position: Int): Fragment {
        return fragments[position].fragment
    }

    fun getTitle(position: Int): Int {
        return fragments[position].titleRes
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position].fragment
    }
}