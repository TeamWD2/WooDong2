package com.wd.woodong2.presentation.group.detail.board.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailBoardDetailActivityBinding
import com.wd.woodong2.presentation.group.content.GroupItem

class GroupDetailBoardDetailActivity : AppCompatActivity() {
    companion object {
        private const val GROUP_BOARD_ITEM = "group_board_item"

        fun newIntent(
            context: Context,
            groupBoardItem: GroupItem.GroupBoard
        ): Intent =
            Intent(context, GroupDetailBoardDetailActivity::class.java).apply {
                putExtra(GROUP_BOARD_ITEM, groupBoardItem)
            }
    }

    private lateinit var binding: GroupDetailBoardDetailActivityBinding

    private val viewModel: GroupDetailBoardDetailViewModel by viewModels()

    private val boardDetailListAdapter by lazy {
        GroupDetailBoardDetailListAdapter()
    }

    private val groupBoardItem by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(GROUP_BOARD_ITEM, GroupItem.GroupBoard::class.java)
        } else {
            intent.getParcelableExtra(GROUP_BOARD_ITEM)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupDetailBoardDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        //상태바 & 아이콘 색상 변경
        window.statusBarColor = ContextCompat.getColor(this@GroupDetailBoardDetailActivity, R.color.egg_yellow_toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 안드로이드 11 이상에서만 동작
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 안드로이드 6.0 이상에서만 동작
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } // 안드로이드 6.0 이하는 상태바 아이콘 색상 변경 지원 안함

        recyclerViewBoardDetail.adapter = boardDetailListAdapter

        viewModel.setGroupBoardItem(groupBoardItem)
    }

    private fun initViewModel() = with(viewModel) {
        groupBoardItem.observe(this@GroupDetailBoardDetailActivity) {
            boardDetailListAdapter.submitList(it)
        }
    }
}