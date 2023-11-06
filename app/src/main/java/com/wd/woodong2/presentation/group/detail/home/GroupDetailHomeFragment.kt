package com.wd.woodong2.presentation.group.detail.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.wd.woodong2.databinding.GroupDetailHomeFragmentBinding
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModel
import com.wd.woodong2.presentation.group.detail.board.detail.GroupDetailBoardDetailActivity

class GroupDetailHomeFragment : Fragment() {
    companion object {
        fun newInstance() = GroupDetailHomeFragment()
    }

    private var _binding: GroupDetailHomeFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GroupDetailSharedViewModel by activityViewModels()

    private val groupDetailHomeListAdapter by lazy {
        GroupDetailHomeListAdapter(
            onClickBoardItem = { groupItem ->
                startActivity(
                    GroupDetailBoardDetailActivity.newIntent(
                        requireContext(),
                        groupItem
                    )
                )
            },
            onClickMoreBtn = { tabName ->
                sharedViewModel.modifyTab(tabName)
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
    }

    private fun initView() = with(binding) {
        recyclerViewAddDetailHome.adapter = groupDetailHomeListAdapter
        groupDetailHomeListAdapter.submitList(sharedViewModel.groupDetailItem)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}