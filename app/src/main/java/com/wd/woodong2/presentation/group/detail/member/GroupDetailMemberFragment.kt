package com.wd.woodong2.presentation.group.detail.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.wd.woodong2.databinding.GroupDetailMemberFragmentBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModel
import com.wd.woodong2.presentation.group.detail.GroupDetailSharedViewModelFactory

class GroupDetailMemberFragment : Fragment() {
    companion object {
        fun newInstance() = GroupDetailMemberFragment()
    }

    private var _binding: GroupDetailMemberFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: GroupDetailSharedViewModel by activityViewModels {
        GroupDetailSharedViewModelFactory()
    }

    private val groupDetailMemberListAdapter by lazy {
        GroupDetailMemberListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupDetailMemberFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        recyclerViewAddDetailMember.adapter = groupDetailMemberListAdapter
    }

    private fun initViewModel() = with(sharedViewModel) {
        groupDetailItem.observe(viewLifecycleOwner) { detailItem ->
            val groupMemberList = detailItem?.filterIsInstance<GroupItem.GroupMember>()
            groupDetailMemberListAdapter.submitList(
                groupMemberList?.flatMap { it.memberList ?: listOf() }
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}