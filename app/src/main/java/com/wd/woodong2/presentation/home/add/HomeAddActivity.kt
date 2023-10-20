package com.wd.woodong2.presentation.home.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.wd.woodong2.databinding.HomeAddActivityBinding
import com.wd.woodong2.presentation.home.map.HomeMapActivity
import java.util.UUID


class HomeAddActivity : AppCompatActivity() {

    companion object {
        fun homeAddActivityNewIntent(context: Context?) =
            Intent(context, HomeAddActivity::class.java)
    }

    private lateinit var binding: HomeAddActivityBinding
    private var selectedImageUri: Uri? = null
    private var selectedTag: String? = null

    private val viewModel: HomeAddViewModel by lazy {
        ViewModelProvider(this).get(HomeAddViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.homeAddAddbtn.setOnClickListener {
            val title = binding.homeAddTitle.text.toString()
            val description = binding.homeAddContent.text.toString()

            viewModel.uploadData(title, description, selectedImageUri, selectedTag) {
                finish()
            }
        }

        val imagePicker =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    selectedImageUri = result.data?.data
                    binding.homeThumbnail.setImageURI(selectedImageUri)
                }
            }

        binding.homeAddPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePicker.launch(intent)
        }

        binding.homeAddTag1.setOnClickListener {
            selectTag(binding.homeAddTag1, "동네질문")
        }

        binding.homeAddTag2.setOnClickListener {
            selectTag(binding.homeAddTag2, "조심해요!")
        }

        binding.homeAddTag3.setOnClickListener {
            selectTag(binding.homeAddTag3, "정보공유")
        }
    }
    private fun selectTag(selectedChip: Chip, tag: String) {
        binding.homeAddTag1.isSelected = false
        binding.homeAddTag2.isSelected = false
        binding.homeAddTag3.isSelected = false

        selectedChip.isSelected = true

        selectedTag = tag
    }

}




