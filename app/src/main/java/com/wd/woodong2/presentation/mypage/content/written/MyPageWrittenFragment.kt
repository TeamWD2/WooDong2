package com.wd.woodong2.presentation.mypage.content.written

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wd.woodong2.databinding.MyPageWrittenFragmentBinding
import com.wd.woodong2.presentation.home.content.HomeListAdapter
import com.wd.woodong2.presentation.home.detail.HomeDetailActivity

class MyPageWrittenFragment : Fragment() {

    private var _binding : MyPageWrittenFragmentBinding? = null
    private val binding get() = _binding!!
    private val listAdapter by lazy {
        MyPageWrittenListAdapter(
            onClickItem = { item ->
                startActivity(
                    HomeDetailActivity.homeDetailActivityNewIntent(
                        requireContext(),
                        item,
                    )
                )
            },
        )
    }
    private val viewModel : MyPageWrittenViewModel by viewModels {
        MyPageWrittenViewModelFactory(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = MyPageWrittenFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }
    private fun initView() = with(binding){
        myPageWrittenRecyclerView.apply {
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
                binding.progressBar.isVisible = loadingState ?: false
            }
            isEmptyList.observe(viewLifecycleOwner){isEmptyList->
                binding.txtEmptyWrittenList.isVisible = isEmptyList ?: false
            }
        }
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}