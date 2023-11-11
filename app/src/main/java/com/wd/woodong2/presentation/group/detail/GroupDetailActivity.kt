package com.wd.woodong2.presentation.group.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import coil.load
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailActivityBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import com.wd.woodong2.presentation.group.detail.board.add.GroupDetailBoardAddActivity
import kotlin.math.abs

class GroupDetailActivity : AppCompatActivity() {
    companion object {
        private const val ITEM_ID = "item_id"
        fun newIntent(
            context: Context,
            itemId: String?
        ): Intent =
            Intent(context, GroupDetailActivity::class.java).apply {
                putExtra(ITEM_ID, itemId)
            }
    }

    private lateinit var binding: GroupDetailActivityBinding

    private val viewModel: GroupDetailSharedViewModel by viewModels {
        GroupDetailSharedViewModelFactory(this@GroupDetailActivity)
    }

    private val itemId by lazy {
        intent.getStringExtra(ITEM_ID)
    }

    private val viewPager2Adapter by lazy {
        GroupDetailViewPagerAdapter(this@GroupDetailActivity)
    }

    private lateinit var groupDetailContentType: GroupDetailContentType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
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

        //초기 데이터 출력
        viewModel.getGroupDetailItem(itemId)

        //Toolbar init
        setSupportActionBar(includeLayoutCoordinator.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        includeLayoutCoordinator.materialToolbar.setNavigationOnClickListener {
            finish() //뒤로가기 아이콘 클릭 시
        }

        //ViewPager2Adapter init
        includeLayoutCoordinator.viewPager2.apply {
            isUserInputEnabled = false
            adapter = viewPager2Adapter
            offscreenPageLimit = viewPager2Adapter.itemCount
        } //swipe

        //TabLayout X ViewPager2
        TabLayoutMediator(
            includeLayoutCoordinator.tabLayout,
            includeLayoutCoordinator.viewPager2
        ) { tab, pos ->
            tab.setText(viewPager2Adapter.getTitle(pos))
        }.attach()

        includeLayoutCoordinator.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                includeLayoutCoordinator.viewPager2.setCurrentItem(tab.position, false)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun initViewModel() = with(viewModel) {
        loadingState.observe(this@GroupDetailActivity) { loadingState ->
            binding.includeLayoutCoordinator.progressBar.isVisible = loadingState
        }

        groupDetailItem.observe(this@GroupDetailActivity) { detailItem ->
            //넘겨 받은 데이터 출력
            initClickItem(detailItem)
            viewModel.initIsJoinGroup()

            binding.includeLayoutCoordinator.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                binding.includeLayoutCoordinator.materialToolbar.post {
                    binding.includeLayoutCoordinator.materialToolbar.title =
                        if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                            detailItem?.filterIsInstance<GroupItem.GroupMain>()?.firstOrNull()?.groupName
                        } else {
                            ""
                        }
                }
            }
        }

        tabName.observe(this@GroupDetailActivity) { tabName ->
            binding.includeLayoutCoordinator.viewPager2.setCurrentItem(
                viewPager2Adapter.findTabPositionByName(tabName),
                false
            )
        }

        isJoinGroup.observe(this@GroupDetailActivity) { isJoinGroup ->
            groupDetailContentType = if(isJoinGroup) GroupDetailContentType.WRITE_BOARD else GroupDetailContentType.JOIN_GROUP

            binding.btnAddInfo.text = when (groupDetailContentType) {
                GroupDetailContentType.WRITE_BOARD -> getString(R.string.group_detail_btn_write_board)
                GroupDetailContentType.JOIN_GROUP -> getString(R.string.group_detail_btn_join_group)
            }
            binding.btnAddInfo.setOnClickListener {
                when (groupDetailContentType) {
                    GroupDetailContentType.WRITE_BOARD -> startActivity(
                        GroupDetailBoardAddActivity.newIntent(
                            this@GroupDetailActivity,
                            itemId,
                            viewModel.getUserInfo()
                        )
                    )

                    GroupDetailContentType.JOIN_GROUP -> { //모임 가입하기 버튼 클릭
                        // 1. 모임 가입 제한 인원 확인
                        if(viewModel.isJoinPossibleMemberLimit()) {
                            // 2. 비밀번호 활성화 여부 확인
                            if(viewModel.isExistPassword()) { //비밀번호가 있는 경우
                                showDialogEnterPw()
                            } else { //비밀번호가 없는 경우
                                showDialogJoinGroup()
                            }
                        } else { // 모임 가입 제한 인원 가득찬 경우
                            Toast.makeText(
                                this@GroupDetailActivity,
                                R.string.group_detail_toast_member_limit_full,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    else -> Unit
                }
            }
        }

        isSuccessJoinGroup.observe(this@GroupDetailActivity) { isSuccessJoinGroup ->
            Toast.makeText(
                this@GroupDetailActivity,
                if(isSuccessJoinGroup) R.string.group_detail_toast_join_group_success else R.string.group_detail_toast_join_group_fail,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDialogEnterPw() {
        val edtInput = EditText(this@GroupDetailActivity)
        AlertDialog.Builder(this@GroupDetailActivity).apply {
            setTitle(R.string.group_detail_dialog_title_enter_pw)
            setView(edtInput)
            setPositiveButton(R.string.group_detail_dialog_ok) { _, _ ->
                if(viewModel.checkPassword(edtInput.text.toString())) { //비밀번호가 일치하는 경우
                    showDialogJoinGroup()
                } else {
                    Toast.makeText(
                        this@GroupDetailActivity,
                        R.string.group_detail_toast_incorrect_password,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            setNegativeButton(R.string.group_detail_dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun showDialogJoinGroup() {
        AlertDialog.Builder(this@GroupDetailActivity).apply {
            setTitle(R.string.group_detail_dialog_title_join_group)
            setMessage(R.string.group_detail_dialog_message_join_group)
            setPositiveButton(R.string.group_detail_dialog_ok) { _, _ ->
                viewModel.updateUserInfo(itemId)
            }
            setNegativeButton(R.string.group_detail_dialog_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun initClickItem(detailItem: List<GroupItem>?) = with(binding.includeLayoutCoordinator) {
        imgBackground.load(detailItem?.filterIsInstance<GroupItem.GroupMain>()?.firstOrNull()?.backgroundImage) {
            error(R.drawable.group_ic_no_image)
        }
        imgMain.load(detailItem?.filterIsInstance<GroupItem.GroupMain>()?.firstOrNull()?.mainImage) {
            error(R.drawable.group_ic_no_image)
        }
        txtTitle.text = detailItem?.filterIsInstance<GroupItem.GroupMain>()?.firstOrNull()?.groupName
        txtMemberCount.text = detailItem?.filterIsInstance<GroupItem.GroupMember>()?.firstOrNull()?.memberList?.size?.toString() ?: "1"
        txtBoardCount.text = detailItem?.filterIsInstance<GroupItem.GroupBoard>()?.firstOrNull()?.boardList?.size?.toString() ?: "0"
    }
}