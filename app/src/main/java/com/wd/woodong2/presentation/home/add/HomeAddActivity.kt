package com.wd.woodong2.presentation.home.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeAddActivityBinding
class HomeAddActivity : AppCompatActivity() {

    companion object {
        private var firstLocation: String? = "Unknown Location"
        private var username: String? = "Who"
        fun homeAddActivityNewIntent(context: Context?, firstLoc: String, name: String?) =
            Intent(context, HomeAddActivity::class.java).apply {
                firstLocation = firstLoc
                username = name
            }
    }

    private lateinit var binding: HomeAddActivityBinding
    private var selectedTag: String? = null
    private var selectedThumbnailCount: Int? = 0
    private var selectedImageUri: Uri? = null

    private val viewModel: HomeAddViewModel by viewModels()

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                    selectedImageUri?.let { uri ->
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        val resizedBitmap = Bitmap.createScaledBitmap(
                            bitmap,
                            binding.homeThumbnail.width,
                            binding.homeThumbnail.height,
                            false
                        )
                        binding.homeThumbnail.setImageBitmap(resizedBitmap)
                        //사진 갯수 추가
                        if (selectedImageUri != null && selectedImageUri.toString().isNotEmpty()) {
                            selectedThumbnailCount = selectedThumbnailCount?.plus(1)
                        }
                    }
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {

        //상태바 & 아이콘 색상 변경
        window.statusBarColor = ContextCompat.getColor(this@HomeAddActivity, R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 안드로이드 11 이상에서만 동작
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 안드로이드 6.0 이상에서만 동작
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } // 안드로이드 6.0 이하는 상태바 아이콘 색상 변경 지원 안함

        homeAddAddbtn.setOnClickListener {
            val title = homeAddTitle.text.toString()
            val description = homeAddContent.text.toString()

            viewModel.uploadData(
                username,
                selectedTag,
                "",
                selectedImageUri,
                selectedThumbnailCount,
                title,
                description,
                firstLocation,
            )
            {
                finish()
            }
        }

        homeAddPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePicker.launch(intent)
        }

        toolBar.setNavigationOnClickListener {
            finish()
        }

         homeAddTag1.setOnClickListener {
            selectTag(homeAddTag1, "동네질문")
        }

        homeAddTag2.setOnClickListener {
            selectTag(homeAddTag2, "조심해요!")
        }

        homeAddTag3.setOnClickListener {
            selectTag(homeAddTag3, "정보공유")
        }

        homeAddTag4.setOnClickListener {
            selectTag(homeAddTag4, "동네소식")
        }

        homeAddTag5.setOnClickListener {
            selectTag(homeAddTag5, "사건/사고")
        }

        homeAddTag6.setOnClickListener {
            selectTag(homeAddTag6, "동네사진전")
        }

        homeAddTag7.setOnClickListener {
            selectTag(homeAddTag7, "분실/실종")
        }

        homeAddTag8.setOnClickListener {
            selectTag(homeAddTag8, "생활정보")
        }

    }

    private fun selectTag(selectedChip: Chip, tag: String) = with(binding) {
        homeAddTag1.isSelected = false
        homeAddTag2.isSelected = false
        homeAddTag3.isSelected = false
        homeAddTag4.isSelected = false
        homeAddTag5.isSelected = false
        homeAddTag6.isSelected = false
        homeAddTag7.isSelected = false
        homeAddTag8.isSelected = false

        selectedChip.isSelected = true

        selectedTag = tag
    }
}
