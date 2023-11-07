package com.wd.woodong2.presentation.group.detail.board.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailBoardDetailActivityBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import com.wd.woodong2.presentation.group.detail.board.add.GroupDetailBoardAddActivity
import java.text.SimpleDateFormat
import java.util.Date

class GroupDetailBoardDetailActivity : AppCompatActivity() {
    companion object {
        private const val GROUP_BOARD_ITEM = "group_board_item"
        private const val USER_ID = "user_id"
        private const val USER_PROFILE = "user_profile"
        private const val USER_NAME = "user_name"
        private const val USER_LOCATION = "user_location"

        fun newIntent(
            context: Context,
            userId: String,
            userProfile: String,
            userName: String,
            userLocation: String,
            groupBoardItem: GroupItem.GroupBoard
        ): Intent =
            Intent(context, GroupDetailBoardDetailActivity::class.java).apply {
                putExtra(USER_ID, userId)
                putExtra(USER_PROFILE, userProfile)
                putExtra(USER_NAME, userName)
                putExtra(USER_LOCATION, userLocation)
                putExtra(GROUP_BOARD_ITEM, groupBoardItem)
            }
    }

    private lateinit var binding: GroupDetailBoardDetailActivityBinding

    private val viewModel: GroupDetailBoardDetailViewModel by viewModels()

    private val boardDetailListAdapter by lazy {
        GroupDetailBoardDetailListAdapter(
            onClickDeleteComment = { position ->
                viewModel.deleteComment(position)
            }
        )
    }

    private val userId by lazy {
        intent.getStringExtra(USER_ID)
    }
    private val userProfile by lazy {
        intent.getStringExtra(USER_PROFILE)
    }
    private val userName by lazy {
        intent.getStringExtra(USER_NAME)
    }
    private val userLocation by lazy {
        intent.getStringExtra(USER_LOCATION)
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

        imgBack.setOnClickListener {
            finish()
        }

        val boardItem = groupBoardItem?.boardList?.get(0)

        imgProfile.load(boardItem?.profile)
        txtName.text = boardItem?.name
        txtLocation.text = boardItem?.location
        txtDate.text = boardItem?.timestamp?.let { Date(it) }
            ?.let { SimpleDateFormat("yyyy년 MM월 dd일").format(it) }

        viewModel.initGroupBoardItem(groupBoardItem)

        btnWriteComment.setOnClickListener {
            if(edtWriteComment.text.isNullOrBlank()) {
                Toast.makeText(
                    this@GroupDetailBoardDetailActivity,
                    R.string.group_detail_board_detail_no_comment,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.addBoardComment(
                    userId,
                    userProfile,
                    userName,
                    userLocation,
                    edtWriteComment.text.toString()
                )
                edtWriteComment.setText("")
//                binding.recyclerViewBoardDetail.scrollToPosition()
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        groupBoardItem.observe(this@GroupDetailBoardDetailActivity) {
            boardDetailListAdapter.submitList(it)
        }
    }
}