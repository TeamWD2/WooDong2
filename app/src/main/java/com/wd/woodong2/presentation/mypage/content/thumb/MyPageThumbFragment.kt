package com.wd.woodong2.presentation.mypage.content.thumb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wd.woodong2.databinding.MyPageThumbFragmentBinding
import com.wd.woodong2.presentation.home.detail.HomeDetailActivity
import com.wd.woodong2.presentation.mypage.content.written.MyPageWrittenListAdapter

class MyPageThumbFragment : Fragment() {

    private var _binding : MyPageThumbFragmentBinding? = null
    private val binding get() = _binding!!
    private val listAdapter by lazy {
        MyPageThumbListAdapter(
            currentUser = viewModel.getUserInfo(),
            onClickItem = { item ->
                startActivity(
                    HomeDetailActivity.homeDetailActivityNewIntent(
                        requireContext(),
                        item,
                    )
                )
            },
            onDeleteItem = { item ->
                viewModel.deleteItem(item)
            },
        )
    }
    private val viewModel : MyPageThumbViewModel by viewModels {
        MyPageThumbViewModelFactory(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = MyPageThumbFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()

    }
    private fun initView() = with(binding){
        myPageThumbRecyclerView.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    private fun initViewModel(){
        with(viewModel){
            printList.observe(viewLifecycleOwner){
                listAdapter.submitList(it)
            }
            loadingState.observe(viewLifecycleOwner) { loadingState ->
                binding.progressBar.isVisible = loadingState
            }
            isEmptyList.observe(viewLifecycleOwner){isEmptyList->
                binding.txtEmptyThumbList.isVisible = isEmptyList ?: false
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}