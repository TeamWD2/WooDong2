package com.wd.woodong2.presentation.home.content

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

        val layoutManager = LinearLayoutManager(context)
        binding.homeRecyclerView.layoutManager = layoutManager
        binding.homeRecyclerView.adapter = listAdapter


        val databaseReference = FirebaseDatabase.getInstance().reference.child("home_list")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = ArrayList<HomeItem>()

                for (postSnapshot in dataSnapshot.children) {
                    val firebaseData = postSnapshot.getValue(HomeItem::class.java)
                    if (firebaseData != null) {
                        dataList.add(firebaseData)
                    }
                }

                // RecyclerView 어댑터에 데이터 설정
                listAdapter.submitList(dataList)

                binding.homeRecyclerView.requestLayout()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 처리중 에러 발생시 처리
            }
        })

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