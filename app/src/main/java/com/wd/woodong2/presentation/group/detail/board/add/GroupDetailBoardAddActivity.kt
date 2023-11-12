package com.wd.woodong2.presentation.group.detail.board.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailBoardAddActivityBinding
import com.wd.woodong2.presentation.group.GroupUserInfoItem
import com.wd.woodong2.presentation.group.detail.board.detail.GroupDetailBoardDetailActivity
import java.util.concurrent.atomic.AtomicLong

class GroupDetailBoardAddActivity : AppCompatActivity() {
    companion object {
        private const val ITEM_ID = "item_id"
        private const val USER_INFO = "user_info"

        fun newIntent(
            context: Context,
            id: String?,
            userInfo: GroupUserInfoItem?
        ): Intent =
            Intent(context, GroupDetailBoardAddActivity::class.java).apply {
                putExtra(ITEM_ID, id)
                putExtra(USER_INFO, userInfo)
            }
    }

    private lateinit var binding: GroupDetailBoardAddActivityBinding

    private val viewModel: GroupDetailBoardAddViewModel by viewModels {
        GroupDetailBoardAddViewModelFactory()
    }

    private val itemId by lazy {
        intent.getStringExtra(ITEM_ID)
    }
    private val userInfo by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(USER_INFO, GroupUserInfoItem::class.java)
        } else {
            intent.getParcelableExtra(USER_INFO)
        }
    }

    private val idGenerate = AtomicLong(1L)

    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                Toast.makeText(
                    this,
                    R.string.public_toast_permission_grant,
                    Toast.LENGTH_SHORT
                ).show()
                galleryLauncher.launch(
                    Intent(Intent.ACTION_PICK).setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                )
            } else {
                Toast.makeText(
                    this,
                    R.string.public_toast_permission_deny,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private var currentItem: GroupDetailBoardAddImageItem? = null
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    updateImageItem(uri)
                    addImageItem()
                }
            }
        }

    private val boardAddListAdapter by lazy {
        GroupDetailBoardAddListAdapter(
            onClickPlusImage = { item ->
                currentItem = item
                checkPermissions()
            },
            onClickRemoveImage = { position ->
                viewModel.removeBoardImageItem(position)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupDetailBoardAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        //상태바 & 아이콘 색상 변경
        window.statusBarColor =
            ContextCompat.getColor(this@GroupDetailBoardAddActivity, R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 안드로이드 11 이상에서만 동작
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 안드로이드 6.0 이상에서만 동작
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } // 안드로이드 6.0 이하는 상태바 아이콘 색상 변경 지원 안함

        //넘겨 받은 사용자 위치 ToolBar 출력
        toolBar.title = userInfo?.userLocation?.split(" ")?.last()

        recyclerviewPhoto.adapter = boardAddListAdapter

        addImageItem() //초기 데이터 세팅

        btnAddBoard.setOnClickListener {
            if (edtContent.text.isNullOrBlank()) {
                Toast.makeText(
                    this@GroupDetailBoardAddActivity,
                    R.string.group_add_board_add_toast_no_content,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                btnAddBoard.isClickable = false //같은 게시글 중복 생성 방지
                viewModel.setGroupBoardAlbumItem(
                    itemId,
                    userInfo,
                    edtContent.text.toString()
                )
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        imageList.observe(this@GroupDetailBoardAddActivity) {
            boardAddListAdapter.submitList(it)
        }

        isLoadingState.observe(this@GroupDetailBoardAddActivity) { isLoadingState ->
            binding.progressBar.isVisible = isLoadingState
            if (isLoadingState) {
                Toast.makeText(
                    this@GroupDetailBoardAddActivity,
                    R.string.group_add_board_add_toast_create_board_loading,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        isCreateSuccess.observe(this@GroupDetailBoardAddActivity) { isSuccess ->
            Toast.makeText(
                this@GroupDetailBoardAddActivity,
                if (isSuccess) R.string.group_add_board_add_toast_create_board_success else R.string.group_add_board_add_toast_create_board_fail,
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun checkPermissions() {
        when {
            permissions.all {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            } -> {
                galleryLauncher.launch(
                    Intent(Intent.ACTION_PICK).setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                )
            }

            permissions.any {
                shouldShowRequestPermissionRationale(it)
            } -> { //이전에 권한 요청을 거부한 적이 있는 경우
                showRationalDialog()
            }

            else -> galleryPermissionLauncher.launch(permissions)
        }
    }

    private fun showRationalDialog() {
        AlertDialog.Builder(this@GroupDetailBoardAddActivity).apply {
            setTitle(R.string.public_dialog_rational_title)
            setMessage(R.string.public_dialog_rational_message)
            setPositiveButton(R.string.public_dialog_rational_ok) { _, _ ->
                galleryPermissionLauncher.launch(permissions)
            }
            show()
        }
    }

    private fun addImageItem() {
        viewModel.addBoardImageItem(
            GroupDetailBoardAddImageItem(idGenerate.getAndIncrement())
        )
    }

    private fun updateImageItem(uri: Uri) {
        val imageItem = currentItem?.copy(
            uri = uri,
            isCancelBtn = true,
            isPlusBtn = false
        )
        viewModel.updateBoardImageItem(imageItem)
    }
}