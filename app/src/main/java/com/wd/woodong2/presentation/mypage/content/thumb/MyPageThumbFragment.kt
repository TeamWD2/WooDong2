package com.wd.woodong2.presentation.mypage.content.thumb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wd.woodong2.databinding.MyPageThumbFragmentBinding
import com.wd.woodong2.presentation.home.detail.HomeDetailActivity


class MyPageThumbFragment : Fragment() {

    companion object {
        fun newInstance() = MyPageThumbFragment()
    }
    private var _binding : MyPageThumbFragmentBinding? = null
    private val binding get() = _binding!!
    private val listAdapter by lazy {
        MyPageThumbListAdapter(
            onClickItem = { _,item ->
                startActivity(
                    HomeDetailActivity.homeDetailActivityNewIntent(
                        requireContext(),
                        item)
                )
            }
        )
    }
    private val viewModel : MyPageThumbViewModel by viewModels {
        MyPageThumbViewModelFactory()
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
            list.observe(viewLifecycleOwner){
                listAdapter.submitList(it)
            }
        }
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}