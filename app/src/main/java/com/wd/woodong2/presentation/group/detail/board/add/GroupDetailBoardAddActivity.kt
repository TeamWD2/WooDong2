package com.wd.woodong2.presentation.group.detail.board.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailBoardAddActivityBinding
import java.util.concurrent.atomic.AtomicLong

class GroupDetailBoardAddActivity : AppCompatActivity() {
    companion object {
        private const val ITEM_ID = "item_id"
        private const val USER_ID = "user_id"
        private const val USER_PROFILE = "user_profile"
        private const val USER_NAME = "user_name"
        private const val USER_LOCATION = "user_location"

        fun newIntent(
            context: Context,
            id: String?,
            userId: String,
            userProfile: String,
            userName: String,
            userLocation: String
        ): Intent =
            Intent(context, GroupDetailBoardAddActivity::class.java).apply {
                putExtra(ITEM_ID, id)
                putExtra(USER_ID, userId)
                putExtra(USER_PROFILE, userProfile)
                putExtra(USER_NAME, userName)
                putExtra(USER_LOCATION, userLocation)
            }
    }

    private lateinit var binding: GroupDetailBoardAddActivityBinding

    private val viewModel: GroupDetailBoardAddViewModel by viewModels {
        GroupDetailBoardAddViewModelFactory()
    }

    private val itemId by lazy {
        intent.getStringExtra(ITEM_ID)
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

    private val idGenerate = AtomicLong(1L)

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
                galleryLauncher.launch(
                    Intent(Intent.ACTION_PICK).setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                )
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
        window.statusBarColor = ContextCompat.getColor(this@GroupDetailBoardAddActivity, R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 안드로이드 11 이상에서만 동작
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 안드로이드 6.0 이상에서만 동작
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } // 안드로이드 6.0 이하는 상태바 아이콘 색상 변경 지원 안함

        //넘겨 받은 사용자 위치 ToolBar 출력
        toolBar.title = userLocation

        recyclerviewPhoto.adapter = boardAddListAdapter

        addImageItem() //초기 데이터 세팅

        btnAddBoard.setOnClickListener {
            if(edtContent.text.isNullOrBlank()) {
                Toast.makeText(this@GroupDetailBoardAddActivity, R.string.group_add_board_add_toast_no_content, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.setGroupBoardItem(
                    itemId,
                    userId,
                    userProfile,
                    userName,
                    userLocation,
                    edtContent.text.toString()
                )
                viewModel.setGroupAlbumItem(itemId)
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        imageList.observe(this@GroupDetailBoardAddActivity) {
            boardAddListAdapter.submitList(it)
        }
        isCreateSuccess.observe(this@GroupDetailBoardAddActivity) { isSuccess ->
            Toast.makeText(
                this@GroupDetailBoardAddActivity,
                if(isSuccess) R.string.group_add_board_add_toast_create_board_success else R.string.group_add_board_add_toast_create_board_fail,
                Toast.LENGTH_SHORT
            ).show()
            finish()
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