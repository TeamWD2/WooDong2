package com.wd.woodong2.presentation.mypage.content.group

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wd.woodong2.databinding.MyPageGroupFragmentBinding
import com.wd.woodong2.presentation.home.detail.HomeDetailActivity
import com.wd.woodong2.presentation.mypage.content.thumb.MyPageThumbViewModel
import com.wd.woodong2.presentation.mypage.content.thumb.MyPageThumbViewModelFactory


class MyPageGroupFragment : Fragment() {

    companion object {
        fun newInstance() = MyPageGroupFragment()
    }
    private var _binding : MyPageGroupFragmentBinding? = null
    private val binding get() = _binding!!



    private val listAdapter by lazy {
        MyPageGroupListAdapter(
            //클릭시 GroupDetailHomeActivity로 넘어가기
            onClickItem = { _,item ->
//                startActivity(
//                    GroupDetailHomeActivity.newIntent(
//                        requireContext(),
//                        item)
//                )
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
        myPageGroupRecyclerView.apply {
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