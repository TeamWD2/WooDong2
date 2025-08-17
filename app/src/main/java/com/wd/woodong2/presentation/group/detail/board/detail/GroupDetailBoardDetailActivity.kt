package com.wd.woodong2.presentation.group.detail.board.detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailBoardDetailActivityBinding
import com.wd.woodong2.presentation.group.GroupUserInfoItem
import com.wd.woodong2.presentation.group.content.GroupItem
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
                    setTitle(R.string.group_detail_board_detail_dialog_delete_comment_title)
                    setMessage(R.string.group_detail_board_detail_dialog_delete_message)
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
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        recyclerViewBoardDetail.adapter = boardDetailListAdapter

        imgBack.setOnClickListener {
            finish()
        }

        imgProfile.load(groupBoardItem?.profile) {
            error(R.drawable.public_default_wd2_ivory)
        }
        txtName.text = groupBoardItem?.name
        txtLocation.text = findUserLocation(groupBoardItem?.location)
        txtDate.text = groupBoardItem?.timestamp?.let { Date(it) }
            ?.let { SimpleDateFormat("yyyy년 MM월 dd일").format(it) }
        txtDeleteBoard.isVisible = viewModel.isBoardWriter(groupBoardItem?.userId)

        // 게시글 삭제
        txtDeleteBoard.setOnClickListener {
            AlertDialog.Builder(this@GroupDetailBoardDetailActivity).apply {
                setTitle(R.string.group_detail_board_detail_dialog_delete_board_title)
                setMessage(R.string.group_detail_board_detail_dialog_delete_message)
                setPositiveButton(R.string.group_detail_board_detail_dialog_delete) { _, _ ->
                    viewModel.deleteBoard(
                        itemPkId,
                        groupBoardItem?.boardId
                    )
                    finish() // 현재 화면 닫기
                }
                setNegativeButton(R.string.group_detail_board_detail_dialog_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                show()
            }
        }

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

    private fun findUserLocation(userLocation: String?): String {
        val parts = userLocation?.split(" ")
        parts?.let {
            for(part in it) {
                if(part.endsWith("동")) {
                    return part
                }
            }
        }
        return ""
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