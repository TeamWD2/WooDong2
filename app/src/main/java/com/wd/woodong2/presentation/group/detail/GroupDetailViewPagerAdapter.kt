package com.wd.woodong2.presentation.group.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wd.woodong2.R
import com.wd.woodong2.presentation.group.detail.album.GroupDetailAlbumFragment
import com.wd.woodong2.presentation.group.detail.board.GroupDetailBoardFragment
import com.wd.woodong2.presentation.group.detail.home.GroupDetailHomeFragment
import com.wd.woodong2.presentation.group.detail.member.GroupDetailMemberFragment

class GroupDetailViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(
        GroupDetailTabs(GroupDetailHomeFragment.newInstance(), R.string.group_detail_tab_home_title),
        GroupDetailTabs(GroupDetailMemberFragment.newInstance(), R.string.group_detail_tab_member_title),
        GroupDetailTabs(GroupDetailBoardFragment.newInstance(), R.string.group_detail_tab_board_title),
        GroupDetailTabs(GroupDetailAlbumFragment.newInstance(), R.string.group_detail_tab_album_title)
    )

    fun getFragment(position: Int): Fragment {
        return fragments[position].fragment
    }

    fun getTitle(position: Int): Int {
        return fragments[position].title
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position].fragment
    }
}