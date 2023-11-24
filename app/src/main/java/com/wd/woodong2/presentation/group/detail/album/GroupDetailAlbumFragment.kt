package com.wd.woodong2.presentation.group.detail.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailAlbumFragmentBinding
import com.wd.woodong2.databinding.GroupDetailAlbumImagePopupBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModel
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModelFactory

class GroupDetailAlbumFragment: Fragment() {
    companion object {
        fun newInstance() = GroupDetailAlbumFragment()
    }

    private var _binding: GroupDetailAlbumFragmentBinding? = null
    private var _dialogBinding: GroupDetailAlbumImagePopupBinding? = null
    private val binding get() = _binding!!
    private val dialogBinding get() = _dialogBinding!!

    private val sharedViewModel: GroupDetailSharedViewModel by activityViewModels {
        GroupDetailSharedViewModelFactory(requireContext())
    }

    private val groupDetailAlbumListAdapter by lazy {
        GroupDetailAlbumListAdapter(
            imageClick = { image ->
                showDialogImageView(image)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupDetailAlbumFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewAddDetailAlbum.adapter = groupDetailAlbumListAdapter
    }

    private fun initViewModel() = with(sharedViewModel) {
        groupDetailItem.observe(viewLifecycleOwner) { detailItem ->
            val albumList = detailItem?.filterIsInstance<GroupItem.GroupAlbum>()
                ?.flatMap { it.images ?: listOf() }
            binding.txtEmptyAlbum.isVisible = albumList.isNullOrEmpty()
            if(albumList.isNullOrEmpty().not()) {
                binding.recyclerViewAddDetailAlbum.setBackgroundResource(R.drawable.public_border_box_full)
            }
            groupDetailAlbumListAdapter.submitList(albumList)
        }
    }

    private fun showDialogImageView(image: String) {
        _dialogBinding = GroupDetailAlbumImagePopupBinding.inflate(layoutInflater)
        Glide.with(this).load(image).into(dialogBinding.imgDialog)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()
    }

    override fun onDestroyView() {
        _binding = null
        _dialogBinding = null
        super.onDestroyView()
    }
}