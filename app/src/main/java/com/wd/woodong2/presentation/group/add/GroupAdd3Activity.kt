package com.wd.woodong2.presentation.group.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.wd.woodong2.databinding.GroupAdd3ActivityBinding

class GroupAdd3Activity: AppCompatActivity() {

    companion object {
        fun newAdd3Intent(context: Context): Intent =
            Intent(context, GroupAdd3Activity::class.java)
    }

    private lateinit var binding: GroupAdd3ActivityBinding

    private val viewModel: GroupAddSharedViewModel by viewModels {
        GroupAddSharedViewModelFactory()
    }

    private lateinit var currentItem: String

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    when(currentItem) {
                        "imgMainImage" -> {
                            binding.imgMainImage.setImageURI(uri)
                            binding.imgMainImageInit.isVisible = false
                        }
                        "imgBackgroundImage" -> {
                            binding.imgBackgroundImage.setImageURI(uri)
                            binding.imgBackgroundImageInit.isVisible = false
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupAdd3ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {
        imgBack.setOnClickListener {
            finish()
        }

        imgMainImage.setOnClickListener {
            currentItem = "imgMainImage"
            galleryLauncher.launch(
                Intent(Intent.ACTION_PICK).setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
            )
        }

        imgBackgroundImage.setOnClickListener {
            currentItem = "imgBackgroundImage"
            galleryLauncher.launch(
                Intent(Intent.ACTION_PICK).setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
            )
        }

        btnAddGroup.setOnClickListener {
            //Todo("입력 예외처리")
            Log.d("sinw", "모임 만들기 버튼 클릭")
            setResult(RESULT_OK, Intent())
            finish()
        }
    }
}