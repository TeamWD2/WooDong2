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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.wd.woodong2.databinding.HomeAddActivityBinding
import com.wd.woodong2.presentation.home.map.HomeMapActivity
import java.util.UUID

class HomeAddActivity : AppCompatActivity() {

    companion object {

        fun homeAddActivityNewIntent(context: Context?)=
            Intent(context, HomeAddActivity::class.java).apply {
            }

    }

    private lateinit var binding: HomeAddActivityBinding
    private var selectedImageUri: Uri? = null
    private var selectedTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeAddActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.homeAddAddbtn.setOnClickListener {
            val title = binding.homeAddTitle.text.toString()
            val description = binding.homeAddContent.text.toString()

            // 이미지 업로드 후 데이터 저장
            selectedImageUri?.let { uri ->
                val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
                storageRef.putFile(uri).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        // 이미지 URL과 함께 데이터 저장
                        val data = FirebaseHomeItem(title, description, thumbnail = imageUrl.toString(), tag = selectedTag)
                        val databaseReference = FirebaseDatabase.getInstance().reference.child("home_list")
                        val newRef = databaseReference.push()
                        newRef.setValue(data)
                        finish()
                    }
                }
            } ?: run {
                // 이미지가 선택되지 않았을 경우, 이미지 없이 데이터만 저장
                val data = FirebaseHomeItem(title, description, tag = selectedTag)
                val databaseReference = FirebaseDatabase.getInstance().reference.child("home_list")
                val newRef = databaseReference.push()
                newRef.setValue(data)
                finish()
            }
        }


        val imagePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
            selectedTag = "동네질문"
            binding.homeAddTag1.setBackgroundColor(Color.YELLOW)
            binding.homeAddTag2.setBackgroundColor(Color.GRAY) // 다른 태그는 비활성화
        }

        binding.homeAddTag2.setOnClickListener {
            selectedTag = "조심해요!"
            binding.homeAddTag2.setBackgroundColor(Color.YELLOW)
            binding.homeAddTag1.setBackgroundColor(Color.GRAY) // 다른 태그는 비활성화
        }


    }


}
