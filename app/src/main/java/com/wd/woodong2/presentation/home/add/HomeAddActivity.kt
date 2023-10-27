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
        fun homeAddActivityNewIntent(context: Context?) =
            Intent(context, HomeAddActivity::class.java)
    }

    private lateinit var binding: HomeAddActivityBinding
    private var selectedImageUri: Uri? = null
    private var selectedTag: String? = null

    private val viewModel: HomeAddViewModel by viewModels()
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                binding.homeThumbnail.setImageURI(selectedImageUri)
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

            viewModel.uploadData(title, description, selectedImageUri, selectedTag) {
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




