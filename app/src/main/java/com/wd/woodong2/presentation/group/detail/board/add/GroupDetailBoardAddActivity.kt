package com.wd.woodong2.presentation.group.detail.board.add

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.core.view.isVisible
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailBoardAddActivityBinding

class GroupDetailBoardAddActivity : AppCompatActivity() {
    companion object {
        private const val USER_LOCATION = "user_location"

        fun newIntent(
            context: Context,
            userLocation: String
        ): Intent =
            Intent(context, GroupDetailBoardAddActivity::class.java).apply {
                putExtra(USER_LOCATION, userLocation)
            }
    }

    private lateinit var binding: GroupDetailBoardAddActivityBinding

    private val viewModel: GroupDetailBoardAddViewModel by viewModels {
        GroupDetailBoardAddViewModelFactory()
    }

    private val userLocation by lazy {
        intent.getStringExtra(USER_LOCATION)
    }

    private var clickedImage: String? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    viewModel.getGalleryImage(clickedImage, uri)
                }
            }
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

        imgPhoto1.setOnClickListener {
            clickedImage = "imgPhoto1"
            galleryLauncher.launch(
                Intent(Intent.ACTION_PICK).setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
            )
        }

        imgPhoto2.setOnClickListener {
            clickedImage = "imgPhoto2"
            galleryLauncher.launch(
                Intent(Intent.ACTION_PICK).setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
            )
        }

        imgPhoto3.setOnClickListener {
            clickedImage = "imgPhoto3"
            galleryLauncher.launch(
                Intent(Intent.ACTION_PICK).setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
            )
        }

        btnAddBoard.setOnClickListener {
            if(edtTitle.text.isNullOrBlank() || edtContent.text.isNullOrBlank()) {
                Toast.makeText(this@GroupDetailBoardAddActivity, R.string.group_add_toast_no_info, Toast.LENGTH_SHORT).show()
            } else {
                //Todo("게시물 Firebase 추가")
//                finish()
            }
        }
    }

    private fun initViewModel() = with(viewModel) {
        galleryStorage1.observe(this@GroupDetailBoardAddActivity) { imageUri ->
            with(binding) {
                imgPhoto1.load(imageUri.toString())
                imgPlusPhoto1.isVisible = false
                imgCancelPhoto1.isVisible = true
                cardViewPhoto2.isVisible = true
                imgPlusPhoto2.isVisible = true
                txtImageLimit.text = "(1/3)"
            }
        }
        galleryStorage2.observe(this@GroupDetailBoardAddActivity) { imageUri ->
            with(binding) {
                imgPhoto2.load(imageUri.toString())
                imgPlusPhoto2.isVisible = false
                imgCancelPhoto2.isVisible = true
                cardViewPhoto3.isVisible = true
                imgPlusPhoto3.isVisible = true
                txtImageLimit.text = "(2/3)"
            }
        }
        galleryStorage3.observe(this@GroupDetailBoardAddActivity) { imageUri ->
            with(binding) {
                imgPhoto3.load(imageUri.toString())
                imgPlusPhoto3.isVisible = false
                imgCancelPhoto3.isVisible = true
                txtImageLimit.text = "(3/3)"
            }
        }
    }
}