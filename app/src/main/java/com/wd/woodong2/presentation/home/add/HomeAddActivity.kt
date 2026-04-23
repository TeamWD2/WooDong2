package com.wd.woodong2.presentation.home.add

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.wd.woodong2.databinding.HomeAddActivityBinding
import coil.load

class HomeAddActivity : AppCompatActivity() {

    companion object {
        private var firstLocation: String? = "Unknown Location"
        private var username: String? = "Who"
        private var userId: String? = ""
        fun homeAddActivityNewIntent(
            context: Context?,
            firstLoc: String,
            name: String?,
            id: String
        ) =
            Intent(context, HomeAddActivity::class.java).apply {
                firstLocation = firstLoc
                username = name
                userId = id
            }
    }

    private lateinit var binding: HomeAddActivityBinding
    private var selectedTag: String? = null
    private var selectedThumbnailCount: Int? = 0
    private var selectedImageUri: Uri? = null


    private val viewModel: HomeAddViewModel by viewModels {
        HomeAddViewModelFactory(this)
    }


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri ?: return@registerForActivityResult

            binding.homeThumbnail.load(uri)
            binding.imgPlusPhoto.isVisible = false

            selectedImageUri = uri
            selectedThumbnailCount = (selectedThumbnailCount ?: 0) + 1

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        homeAddAddbtn.setBtnOnClickListener {
            if (selectedTag == null) {
                Toast.makeText(this@HomeAddActivity, "태그를 선택해주세요", Toast.LENGTH_SHORT).show()
            } else if (binding.homeAddTitle.text.isEmpty()) {
                Toast.makeText(this@HomeAddActivity, "제목을 작성해주세요", Toast.LENGTH_SHORT).show()
            } else if (binding.homeAddContent.text.isEmpty()) {
                Toast.makeText(this@HomeAddActivity, "내용을 작성해주세요", Toast.LENGTH_SHORT).show()
            } else {
                // 모든 검사를 통과한 경우에만 게시 로직을 수행
                val title = homeAddTitle.text.toString()
                val description = homeAddContent.text.toString()
                homeAddAddbtn.setBtnClickable(false)
                viewModel.uploadData(
                    userId,
                    username,
                    selectedTag,
                    "",
                    selectedImageUri,
                    selectedThumbnailCount,
                    title,
                    description,
                    firstLocation
                ) {
                    finish()
                }
            }
        }

        homeThumbnail.setOnClickListener {
            launchGallery()
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

    private fun launchGallery() {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun selectTag(selectedChip: Chip, tag: String) = with(binding) {
        // 모든 태그의 클릭 가능 상태를 활성화
        enableAllTagsClickable()

        // 선택된 태그만 클릭 불가능하도록 설정
        selectedChip.isClickable = false

        // 선택된 태그 업데이트
        selectedTag = tag

    }
    private fun enableAllTagsClickable() {
        binding.homeAddTag1.isClickable = true
        binding.homeAddTag2.isClickable = true
        binding.homeAddTag3.isClickable = true
        binding.homeAddTag4.isClickable = true
        binding.homeAddTag5.isClickable = true
        binding.homeAddTag6.isClickable = true
        binding.homeAddTag7.isClickable = true
        binding.homeAddTag8.isClickable = true
    }
}
