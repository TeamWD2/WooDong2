package com.wd.woodong2.presentation.group.detail.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.wd.woodong2.databinding.GroupDetailBoardFragmentBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModel
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModelFactory
import com.wd.woodong2.presentation.group.detail.board.detail.GroupDetailBoardDetailActivity

class GroupDetailBoardFragment : Fragment() {
    companion object {
        fun newInstance() = GroupDetailBoardFragment()
    }

    private var _binding: GroupDetailBoardFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GroupDetailSharedViewModel by activityViewModels {
        GroupDetailSharedViewModelFactory(requireContext())
    }

    private lateinit var groupPkId: String
    private val groupDetailBoardListAdapter by lazy {
        GroupDetailBoardListAdapter(
            onClickBoardItem = { groupItem ->
                startActivity(
                    GroupDetailBoardDetailActivity.newIntent(
                        requireContext(),
                        sharedViewModel.getUserInfo(),
                        groupPkId,
                        groupItem,
                        sharedViewModel.isJoinGroup
                    )
                )
            }
        )
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
        initViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewAddDetailBoard.adapter = groupDetailBoardListAdapter
    }

    private fun initViewModel() = with(sharedViewModel) {
        groupDetailItem.observe(viewLifecycleOwner) { detailItem ->
            val groupBoardList = detailItem?.filterIsInstance<GroupItem.GroupBoard>()
            groupPkId = groupBoardList?.firstOrNull()?.id.toString()
            val boardList = groupBoardList?.flatMap { it.boardList ?: listOf() }
            binding.txtEmptyBoard.isVisible = boardList.isNullOrEmpty()
            groupDetailBoardListAdapter.submitList(boardList)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}