package com.wd.woodong2.presentation.home.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeAddActivityBinding
import androidx.core.graphics.scale

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
                    R.string.public_toast_gallery_permission_grant,
                    Toast.LENGTH_SHORT
                ).show()
                imagePicker.launch(
                    Intent(Intent.ACTION_PICK).setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*"
                    )
                )
            } else {
                Toast.makeText(
                    this,
                    R.string.public_toast_gallery_permission_deny,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let { uri ->
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    val resizedBitmap = bitmap.scale(
                        binding.homeThumbnail.width,
                        binding.homeThumbnail.height,
                        false
                    )
                    binding.homeThumbnail.setImageBitmap(resizedBitmap)
                    binding.imgPlusPhoto.isVisible = false
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
            checkPermissions()
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

    private fun checkPermissions() {
        when {
            permissions.all {
                ContextCompat.checkSelfPermission(
                    this,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            } -> {
                imagePicker.launch(
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
        AlertDialog.Builder(this@HomeAddActivity).apply {
            setTitle(R.string.public_dialog_rational_title)
            setMessage(R.string.public_dialog_gallery_rational_message)
            setPositiveButton(R.string.public_dialog_ok) { _, _ ->
                galleryPermissionLauncher.launch(permissions)
            }
            show()
        }
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
