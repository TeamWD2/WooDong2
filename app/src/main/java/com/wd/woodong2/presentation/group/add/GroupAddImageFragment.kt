package com.wd.woodong2.presentation.group.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.wd.woodong2.databinding.GroupAddImageFragmentBinding

class GroupAddImageFragment: Fragment() {
    companion object {
        fun newInstance() = GroupAddImageFragment()
    }

    private var _binding: GroupAddImageFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GroupAddSharedViewModel by activityViewModels {
        GroupAddSharedViewModelFactory()
    }

    private lateinit var currentItem: String

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    when (currentItem) {
                        "imgMainImage" -> {
                            binding.imgMainImage.setImageURI(uri)
                            binding.imgMainImageInit.isVisible = false
                            val mainImage = uri.toString()
                        }

                        "imgBackgroundImage" -> {
                            binding.imgBackgroundImage.setImageURI(uri)
                            binding.imgBackgroundImageInit.isVisible = false
                            val backgroundImage = uri.toString()
                        }
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupAddImageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
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
    }

    private fun initViewModel() = with(sharedViewModel) {

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}