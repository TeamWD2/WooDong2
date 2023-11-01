package com.wd.woodong2.presentation.group.add

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wd.woodong2.R
import com.wd.woodong2.presentation.group.detail.album.GroupDetailAlbumFragment
import com.wd.woodong2.presentation.group.detail.board.GroupDetailBoardFragment
import com.wd.woodong2.presentation.group.detail.home.GroupDetailHomeFragment
import com.wd.woodong2.presentation.group.detail.member.GroupDetailMemberFragment

class GroupAddViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(
        GroupAddIntroFragment.newInstance(),
        GroupAddWithFragment.newInstance(),
        GroupAddImageFragment.newInstance()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}