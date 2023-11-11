package com.wd.woodong2.presentation.group.content

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.wd.woodong2.databinding.GroupFragmentBinding
import com.wd.woodong2.presentation.group.add.GroupAddActivity
import com.wd.woodong2.presentation.group.detail.GroupDetailActivity

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

    private var keyword = ""

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

        // 검색 아이콘 클릭 리스너
        imgToolbarSearch.setOnClickListener {
            constraintSearch.isVisible = true
            edtToolbarSearch.addTextChangedListener(searchTextWatcher())
        }

        // 검색창 취소 아이콘 클릭 리스너
        imgToolbarCancel.setOnClickListener {
            constraintSearch.isVisible = false
            keyword = ""
            viewModel.setKeyword(keyword)
        }

        // Floating 버튼 클릭 리스너
        fabAddGroup.setOnClickListener {
            startActivity(GroupAddActivity.newIntent(requireContext()))
        }

        recyclerViewGroup.adapter = groupListAdapter
        viewModel.getGroupItem() //Firebase 통신 - 모임 데이터 가져오기
    }

    private fun initViewModel() = with(viewModel) {
        searchKeyword.observe(viewLifecycleOwner) { keyword ->
            groupListAdapter.submitList(searchKeywordGroupItem(keyword))
        }

        groupList.observe(viewLifecycleOwner) {
            groupListAdapter.submitList(searchKeywordGroupItem(keyword))
        }

        loadingState.observe(viewLifecycleOwner) { loadingState ->
            binding.progressBar.isVisible = loadingState
        }

        isEmptyList.observe(viewLifecycleOwner) { isEmptyList ->
            binding.txtEmptyGroupList.isVisible = isEmptyList
        }
    }

    private fun searchTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            keyword = binding.edtToolbarSearch.text.toString()
            viewModel.setKeyword(keyword)
        }
    }

    private fun clickGroupItem(item: GroupItem.GroupMain) {
        startActivity(
            GroupDetailActivity.newIntent(
                requireContext(),
                item.id
            )
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}