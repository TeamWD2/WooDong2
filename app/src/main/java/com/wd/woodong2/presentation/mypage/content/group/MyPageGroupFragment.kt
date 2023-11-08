package com.wd.woodong2.presentation.mypage.content.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wd.woodong2.databinding.MyPageGroupFragmentBinding
import com.wd.woodong2.presentation.group.content.GroupItem
import com.wd.woodong2.presentation.group.content.GroupListAdapter
import com.wd.woodong2.presentation.group.detail.GroupDetailActivity
import com.wd.woodong2.presentation.group.detail.GroupDetailContentType


class MyPageGroupFragment : Fragment() {


    private var _binding : MyPageGroupFragmentBinding? = null
    private val binding get() = _binding!!

    private val myGroupListAdapter by lazy {
        MyPageGroupListAdapter(
            itemClickListener = { item ->
                clickGroupItem(item)
            }
        )
    }

    private val viewModel : MyPageGroupViewModel by viewModels {
        MyPageGroupViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = MyPageGroupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }
    private fun initView() = with(binding){

        myPageGroupRecyclerView.adapter = myGroupListAdapter
        viewModel.getGroupItem()
    }
    private fun initViewModel(){
        with(viewModel){
            groupList.observe(viewLifecycleOwner) {
                printListSet()
                myGroupListAdapter.submitList(printList.value?.filterIsInstance<GroupItem.GroupMain>())
            }
            loadingState.observe(viewLifecycleOwner) { loadingState ->
                binding.progressBar.isVisible = loadingState
            }
            isEmptyList.observe(viewLifecycleOwner) { isEmptyList ->
                binding.txtEmptyGroupList.isVisible = isEmptyList
            }
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