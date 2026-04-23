package com.wd.woodong2.presentation.group.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupAddImageFragmentBinding

class GroupAddImageFragment: Fragment() {
    companion object {
        fun newInstance() = GroupAddImageFragment()
    }

    private var _binding: GroupAddImageFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GroupAddSharedViewModel by activityViewModels {
        GroupAddSharedViewModelFactory(requireContext())
    }

    private lateinit var currentItem: String

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri ?: return@registerForActivityResult

            when (currentItem) {
                "imgMainImage" -> {
                    binding.imgMainImage.load(uri)
                    binding.imgMainImageInit.isVisible = false
                }
                "imgBackgroundImage" -> {
                    binding.imgBackgroundImage.load(uri)
                    binding.imgBackgroundImageInit.isVisible = false
                }
            }
            sharedViewModel.setImage(currentItem, uri)
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
            launchGallery()
        }

        imgBackgroundImage.setOnClickListener {
            currentItem = "imgBackgroundImage"
            launchGallery()
        }

        btnAddGroup.setBtnOnClickListener {
            sharedViewModel.setGroupAddItem()
        }
    }

    private fun initViewModel() = with(sharedViewModel) {
        isLoadingState.observe(viewLifecycleOwner) { isLoadingState ->
            binding.btnAddGroup.setBtnClickable(!isLoadingState) //같은 모임 중복 생성 방지
            binding.progressBar.isVisible = isLoadingState
            if(isLoadingState) {
                Toast.makeText(requireContext(), R.string.group_add_toast_create_group_loading, Toast.LENGTH_SHORT).show()
            }
        }

        isCreateSuccess.observe(viewLifecycleOwner) { isCreateSuccess ->
            if(isCreateSuccess) {
                Toast.makeText(requireContext(), R.string.group_add_toast_create_group, Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), R.string.group_add_toast_no_info, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchGallery() {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}