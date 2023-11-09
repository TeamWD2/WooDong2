package com.wd.woodong2.presentation.home.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.wd.woodong2.databinding.HomeAddActivityBinding
class HomeAddActivity : AppCompatActivity() {

    companion object {
        private var firstLocation : String? ="Unknown Location"
        private var username : String? = "Who"
        private var userId : String? = ""
        fun homeAddActivityNewIntent(context: Context?,firstLoc: String, name: String?, id: String) =
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


    private val viewModel: HomeAddViewModel by viewModels{
        HomeAddViewModelFactory(this)
    }

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.homeThumbnail.setImageURI(selectedImageUri)
                //사진 갯수 추가
                if(selectedImageUri != null && selectedImageUri.toString().isNotEmpty()){
                    selectedThumbnailCount = selectedThumbnailCount?.plus(1)
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
        homeAddAddbtn.setOnClickListener {
            val title = homeAddTitle.text.toString()
            val description = homeAddContent.text.toString()

            viewModel.uploadData(
                userId,
                username,
                selectedTag,
                "",
                selectedImageUri,
                selectedThumbnailCount,
                title,
                description,
                firstLocation,)
            {
                finish()
            }
        }

        homeAddPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePicker.launch(intent)
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
    }

    private fun selectTag(selectedChip: Chip, tag: String) = with(binding) {
        homeAddTag1.isSelected = false
        homeAddTag2.isSelected = false
        homeAddTag3.isSelected = false

        selectedChip.isSelected = true

        selectedTag = tag
    }

}




