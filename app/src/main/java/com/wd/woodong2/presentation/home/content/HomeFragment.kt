package com.wd.woodong2.presentation.home.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.wd.woodong2.databinding.HomeFragmentBinding
import com.wd.woodong2.presentation.home.detail.HomeDetailActivity


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _binding : HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel : HomeViewModel by lazy{
        ViewModelProvider(
            this,
            HomeViewModelFactory(requireContext())
        )[HomeViewModel::class.java]
    }

    private val listAdapter by lazy {
        HomeListAdapter(requireContext(),
            onClickItem = { item ->
                startActivity(
                    HomeDetailActivity.homeDetailActivityNewIntent(
                        requireContext(),
                        item)
                )
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)

        //divider 설정
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.homeRecyclerView.addItemDecoration(dividerItemDecoration)


        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        homeRecyclerView.adapter = listAdapter
    }

    private fun initViewModel(){
        with(viewModel){
            list.observe(viewLifecycleOwner){
                listAdapter.submitList(it)
                //
            }
        }
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}