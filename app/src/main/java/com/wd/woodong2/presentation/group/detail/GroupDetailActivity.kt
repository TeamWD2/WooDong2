package com.wd.woodong2.presentation.group.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.load
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailActivityBinding
import com.wd.woodong2.databinding.GroupDetailActivityCoordinatorBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import com.wd.woodong2.presentation.group.detail.board.add.GroupDetailBoardAddActivity
import kotlin.math.abs

class GroupDetailActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_GROUP_DETAIL_CONTENT_TYPE = "extra_group_detail_content_type"
        private const val EXTRA_GROUP_ITEM = "extra_group_item"
        fun newIntent(
            context: Context,
            groupDetailContentType: String,
            groupItems: List<GroupItem>
        ): Intent =
            Intent(context, GroupDetailActivity::class.java).apply {
                putExtra(EXTRA_GROUP_DETAIL_CONTENT_TYPE, groupDetailContentType)
                putParcelableArrayListExtra(EXTRA_GROUP_ITEM, ArrayList(groupItems))
            }
    }

    private lateinit var binding: GroupDetailActivityBinding
    private lateinit var includeBinding: GroupDetailActivityCoordinatorBinding

    private val viewModel: GroupDetailSharedViewModel by viewModels()

    private val groupDetailContentType by lazy {
        GroupDetailContentType.from(
            intent.getStringExtra(
                EXTRA_GROUP_DETAIL_CONTENT_TYPE
            )
        )
    }

    private val groupItems by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(
                EXTRA_GROUP_ITEM,
                GroupItem::class.java
            )
        } else {
            intent.getParcelableArrayListExtra(
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
        initViewModel()
    }

    private fun initView() {
        //상태바 & 아이콘 색상 변경
        window.statusBarColor = ContextCompat.getColor(this@GroupDetailActivity, R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 안드로이드 11 이상에서만 동작
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 안드로이드 6.0 이상에서만 동작
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } // 안드로이드 6.0 이하는 상태바 아이콘 색상 변경 지원 안함

        with(includeBinding) {
            viewModel.setDetailItem(groupItems)

            //넘겨 받은 데이터 출력
            val groupMainItem =
                groupItems?.find { it is GroupItem.GroupMain } as? GroupItem.GroupMain
            initClickItem(groupMainItem)

            //Toolbar init
            setSupportActionBar(materialToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            materialToolbar.setNavigationOnClickListener {
                finish() //뒤로가기 아이콘 클릭 시
            }
            appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                materialToolbar.title = if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                    groupMainItem?.groupName
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
                    viewPager2.setCurrentItem(tab.position, false)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
        with(binding) {
            btnAddInfo.text = when (groupDetailContentType) {
                GroupDetailContentType.WRITE_BOARD -> getString(R.string.group_detail_btn_write_board)
                GroupDetailContentType.JOIN_GROUP -> getString(R.string.group_detail_btn_join_group)
                else -> ""
            }
            btnAddInfo.setOnClickListener {
                //Todo("화면 이동 구현")
                when (groupDetailContentType) {
                    GroupDetailContentType.WRITE_BOARD -> startActivity(
                        GroupDetailBoardAddActivity.newIntent(
                            this@GroupDetailActivity,
                            groupItems?.get(0)?.id,
                            "-NhImSiDataNew", //임시 데이터 (로그인 된 계정의 정보)
                            "https://i.ytimg.com/vi/dhZH7NLCOmk/default.jpg",
                            "gildong",
                            "인계동"
                        )
                    )

                    GroupDetailContentType.JOIN_GROUP -> Toast.makeText(
                        this@GroupDetailActivity,
                        "모임 가입하기 클릭",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> Unit
                }
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        viewModel.tabName.observe(this@GroupDetailActivity) { tabName ->
            includeBinding.viewPager2.setCurrentItem(
                viewPager2Adapter.findTabPositionByName(tabName),
                false
            )
        }
    }

    private fun initClickItem(groupMainItem: GroupItem.GroupMain?) = with(includeBinding) {
        imgBackground.load(groupMainItem?.backgroundImage) {
            error(R.drawable.group_ic_no_image)
        }
        imgMain.load(groupMainItem?.mainImage) {
            error(R.drawable.group_ic_no_image)
        }
        txtTitle.text = groupMainItem?.groupName
        txtCount.text = "멤버 ${groupMainItem?.memberCount ?: "1"} " +
                "/ 게시판 ${groupMainItem?.boardCount ?: "0"}"
    }
}