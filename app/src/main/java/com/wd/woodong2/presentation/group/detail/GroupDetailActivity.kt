package com.wd.woodong2.presentation.group.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailActivityBinding
import com.wd.woodong2.databinding.GroupDetailActivityCoordinatorBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import kotlin.math.abs

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

    private val viewModel: GroupDetailSharedViewModel by viewModels()

    private val groupItem by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
            viewModel.setDetailItem(groupItem)

            //넘겨 받은 데이터 출력
            initClickItem()

            //Toolbar init
            setSupportActionBar(materialToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            materialToolbar.setNavigationOnClickListener {
                finish() //뒤로가기 아이콘 클릭 시
            }
            appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                materialToolbar.title = if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                    (groupItem as? GroupItem.GroupIntroduce)?.title
                } else {
                    ""
                }
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
                //Todo("모임 가입하기 or 게시물 작성하기")
            }
        }
    }

    private fun initClickItem() = with(includeBinding) {
        imgBackground.load((groupItem as? GroupItem.GroupIntroduce)?.backgroundImage) {
            error(R.drawable.group_ic_no_image)
        }
        imgMain.load((groupItem as? GroupItem.GroupIntroduce)?.mainImage) {
            error(R.drawable.group_ic_no_image)
        }
        txtTitle.text = (groupItem as? GroupItem.GroupIntroduce)?.title
        txtCount.text = "멤버 ${(groupItem as? GroupItem.GroupMember)?.memberList?.size ?: "1"} " +
                "/ 게시판 ${(groupItem as? GroupItem.GroupBoard)?.boardList?.size ?: "0"}"
    }
}