package com.wd.woodong2.presentation.group.detail.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.wd.woodong2.R
import com.wd.woodong2.databinding.GroupDetailAlbumImagePopupBinding
import com.wd.woodong2.databinding.GroupDetailHomeFragmentBinding
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModel
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModelFactory
import com.wd.woodong2.presentation.group.detail.board.detail.GroupDetailBoardDetailActivity

class GroupDetailHomeFragment : Fragment() {
    companion object {
        fun newInstance() = GroupDetailHomeFragment()
    }

    private var _binding: GroupDetailHomeFragmentBinding? = null
    private var _dialogBinding: GroupDetailAlbumImagePopupBinding? = null
    private val binding get() = _binding!!
    private val dialogBinding get() = _dialogBinding!!

    private val sharedViewModel: GroupDetailSharedViewModel by activityViewModels {
        GroupDetailSharedViewModelFactory(requireContext())
    }

    private val groupDetailHomeListAdapter by lazy {
        GroupDetailHomeListAdapter(
            onClickBoardItem = { id, groupItem ->
                startActivity(
                    GroupDetailBoardDetailActivity.newIntent(
                        requireContext(),
                        sharedViewModel.getUserInfo(),
                        id,
                        groupItem,
                        sharedViewModel.isJoinGroup.value
                    )
                )
            },
            onClickMoreBtn = { tabName ->
                sharedViewModel.modifyTab(tabName)
            },
            onClickAlbumItem = { image ->
                showDialogImageView(image)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupDetailHomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewAddDetailHome.adapter = groupDetailHomeListAdapter
    }

    private fun initViewModel() = with(sharedViewModel) {
        groupDetailItem.observe(viewLifecycleOwner) { detailItem ->
            groupDetailHomeListAdapter.submitList(detailItem)
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