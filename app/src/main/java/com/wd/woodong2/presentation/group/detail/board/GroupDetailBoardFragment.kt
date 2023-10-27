package com.wd.woodong2.presentation.group.detail.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.wd.woodong2.databinding.GroupDetailBoardFragmentBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModel

class GroupDetailBoardFragment: Fragment() {
    companion object {
        fun newInstance() = GroupDetailBoardFragment()
    }

    private var _binding: GroupDetailBoardFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GroupDetailSharedViewModel by activityViewModels()

    private val groupDetailBoardListAdapter by lazy {
        GroupDetailBoardListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupDetailBoardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        recyclerViewAddDetailBoard.adapter = groupDetailBoardListAdapter
        val groupboardList = sharedViewModel.groupDetailItem?.filterIsInstance<GroupItem.GroupBoard>()
        groupDetailBoardListAdapter.submitList(groupboardList?.flatMap { it.boardList ?: listOf() })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}