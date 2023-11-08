package com.wd.woodong2.presentation.group.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.wd.woodong2.databinding.GroupFragmentBinding
import com.wd.woodong2.presentation.group.add.GroupAddActivity
import com.wd.woodong2.presentation.group.detail.GroupDetailActivity
import com.wd.woodong2.presentation.group.detail.GroupDetailContentType

class GroupFragment : Fragment() {
    companion object {
        fun newInstance() = GroupFragment()
    }

    private var _binding: GroupFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupViewModel by viewModels {
        GroupViewModelFactory(requireContext())
    }

    private val groupListAdapter by lazy {
        GroupListAdapter(
            itemClickListener = { item ->
                clickGroupItem(item)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = GroupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        // TODO("toolbar 설정")

        fabAddGroup.setOnClickListener {
            startActivity(GroupAddActivity.newIntent(requireContext()))
        }

        recyclerViewGroup.adapter = groupListAdapter
        viewModel.getGroupItem()
    }

    private fun initViewModel() = with(viewModel) {
        groupList.observe(viewLifecycleOwner) {
            groupListAdapter.submitList(it?.filterIsInstance<GroupItem.GroupMain>())
        }
        loadingState.observe(viewLifecycleOwner) { loadingState ->
            binding.progressBar.isVisible = loadingState
        }
        isEmptyList.observe(viewLifecycleOwner) { isEmptyList ->
            binding.txtEmptyGroupList.isVisible = isEmptyList
        }
    }

    private fun clickGroupItem(item: GroupItem.GroupMain) {
        startActivity(
            GroupDetailActivity.newIntent(
                requireContext(),
                if (viewModel.isUserInGroup(item.id)) GroupDetailContentType.WRITE_BOARD.name else GroupDetailContentType.JOIN_GROUP.name,
                item.id
            )
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}