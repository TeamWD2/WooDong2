package com.wd.woodong2.presentation.group.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailActivityBinding
import com.wd.woodong2.databinding.GroupDetailActivityCoordinatorBinding
import com.wd.woodong2.presentation.group.content.GroupItem

class GroupDetailActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_GROUP_ITEM = "extra_group_item"
        fun newIntent(context: Context, item: GroupItem): Intent =
            Intent(context, GroupDetailActivity::class.java).apply {
                putExtra(EXTRA_GROUP_ITEM, item)
            }
    }

    private lateinit var binding: GroupDetailActivityBinding
    private lateinit var includeBinding: GroupDetailActivityCoordinatorBinding

    private val groupItem by lazy {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                EXTRA_GROUP_ITEM,
                GroupItem::class.java
            )
        } else {
            intent.getParcelableExtra(
                EXTRA_GROUP_ITEM
            )
        }
    }

    private val viewPager2Adapter by lazy {
        GroupDetailViewPagerAdapter(this@GroupDetailActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        includeBinding = binding.includeLayoutCoordinator

        initView()
    }

    private fun initView() {
        with(includeBinding) {
            //넘겨받은 데이터 출력
            imgBackground.load(groupItem?.backgroundImage) {
                error(R.drawable.group_ic_no_image)
            }
            txtTitle.text = groupItem?.title
            txtCount.text = "멤버 ${groupItem?.memberList?.size} / 게시판 1"

            //Toolbar init
            setSupportActionBar(materialToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            materialToolbar.title = "Test Title"
            materialToolbar.setNavigationOnClickListener {
                finish() //뒤로가기 아이콘 클릭 시
            }

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
        with(binding) {
            btnJoinGroup.setOnClickListener {
                //Todo("모임 가입하기")
            }
        }
    }
}