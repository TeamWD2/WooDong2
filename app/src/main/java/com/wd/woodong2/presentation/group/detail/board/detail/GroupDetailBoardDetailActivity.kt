package com.wd.woodong2.presentation.group.detail.board.detail

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailBoardDetailActivityBinding
import com.wd.woodong2.presentation.group.GroupUserInfoItem
import com.wd.woodong2.presentation.group.content.GroupItem
import com.wd.woodong2.presentation.group.detail.board.add.GroupDetailBoardAddActivity
import java.text.SimpleDateFormat
import java.util.Date

class GroupDetailBoardDetailActivity : AppCompatActivity() {
    companion object {
        private const val USER_INFO = "user_info"
        private const val ITEM_PK_ID = "item_pk_id"
        private const val GROUP_BOARD_ITEM = "group_board_item"
        private const val IS_JOIN_GROUP = "is_join_group"

        fun newIntent(
            context: Context,
            userInfo: GroupUserInfoItem?,
            id: String,
            groupBoardItem: GroupItem.Board,
            isJoinGroup: Boolean?
        ): Intent =
            Intent(context, GroupDetailBoardDetailActivity::class.java).apply {
                putExtra(USER_INFO, userInfo)
                putExtra(ITEM_PK_ID, id)
                putExtra(GROUP_BOARD_ITEM, groupBoardItem)
                putExtra(IS_JOIN_GROUP, isJoinGroup)
            }
    }

    private lateinit var binding: GroupDetailBoardDetailActivityBinding

    private val viewModel: GroupDetailBoardDetailViewModel by viewModels {
        GroupDetailBoardDetailViewModelFactory(this@GroupDetailBoardDetailActivity)
    }

    private val boardDetailListAdapter by lazy {
        GroupDetailBoardDetailListAdapter(
            onClickDeleteComment = { position ->
                AlertDialog.Builder(this@GroupDetailBoardDetailActivity).apply {
                    setTitle(R.string.group_detail_board_detail_dialog_title)
                    setMessage(R.string.group_detail_board_detail_dialog_message)
                    setPositiveButton(R.string.group_detail_board_detail_dialog_delete) { _, _ ->
                        viewModel.deleteComment(
                            itemPkId,
                            groupBoardItem?.boardId,
                            groupBoardItem?.commentList?.get((position - 2) / 2)?.commentId,
                            position
                        )
                    }
                    setNegativeButton(R.string.group_detail_board_detail_dialog_cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    show()
                }
            }
        )
    }

    private val userInfo by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(USER_INFO, GroupUserInfoItem::class.java)
        } else {
            intent.getParcelableExtra(USER_INFO)
        }
    }
    private val itemPkId by lazy {
        intent.getStringExtra(ITEM_PK_ID)
    }
    private val groupBoardItem by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(GROUP_BOARD_ITEM, GroupItem.Board::class.java)
        } else {
            intent.getParcelableExtra(GROUP_BOARD_ITEM)
        }
    }
    private val isJoinGroup by lazy {
        intent.getBooleanExtra(IS_JOIN_GROUP, false)
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
        window.statusBarColor =
            ContextCompat.getColor(this@GroupDetailBoardDetailActivity, R.color.egg_yellow_toolbar)

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

        imgProfile.load(groupBoardItem?.profile) {
            error(R.drawable.public_default_wd2_ivory)
        }
        txtName.text = groupBoardItem?.name
        txtLocation.text = groupBoardItem?.location?.split(" ")?.last()
        txtDate.text = groupBoardItem?.timestamp?.let { Date(it) }
            ?.let { SimpleDateFormat("yyyy년 MM월 dd일").format(it) }

        viewModel.initGroupBoardItem(groupBoardItem)

        constraintCommentWrite.isVisible = isJoinGroup
        btnWriteComment.setOnClickListener {
            if (edtWriteComment.text.isNullOrBlank()) {
                Toast.makeText(
                    this@GroupDetailBoardDetailActivity,
                    R.string.group_detail_board_detail_no_comment,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.addBoardComment(
                    itemPkId,
                    groupBoardItem?.boardId,
                    userInfo,
                    edtWriteComment.text.toString()
                )
                edtWriteComment.setText("")
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        groupBoardItem.observe(this@GroupDetailBoardDetailActivity) {
            boardDetailListAdapter.submitList(it)
            binding.recyclerViewBoardDetail.smoothScrollToPosition(it.size-1)
        }

        isSuccessAddComment.observe(this@GroupDetailBoardDetailActivity) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(
                    this@GroupDetailBoardDetailActivity,
                    R.string.group_detail_board_detail_toast_create_comment_success,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}