package com.wd.woodong2.presentation.group.detail.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailAlbumFragmentBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModel
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModelFactory

class GroupDetailAlbumFragment: Fragment() {
    companion object {
        fun newInstance() = GroupDetailAlbumFragment()
    }

    private var _binding: GroupDetailAlbumFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GroupDetailSharedViewModel by activityViewModels {
        GroupDetailSharedViewModelFactory()
    }

    private val groupDetailAlbumListAdapter by lazy {
        GroupDetailAlbumListAdapter()
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}