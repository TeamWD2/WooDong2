package com.wd.woodong2.presentation.home.content


import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wd.woodong2.databinding.HomeFragmentBinding
import com.wd.woodong2.presentation.home.add.HomeAddActivity
import com.wd.woodong2.presentation.home.detail.HomeDetailActivity
import com.wd.woodong2.presentation.home.map.HomeMapActivity


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _binding : HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel : HomeViewModel by viewModels {
        HomeViewModelFactory(requireContext())
    }
    private val homeMapLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val receivedDataFirstLocation = result.data!!.getStringExtra("firstLocation")
                val receivedDataSecondLocation = result.data!!.getStringExtra("secondLocation")
                binding.toolbarTvLocation.text = receivedDataFirstLocation
            } else {

            }
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
        // Toolbar 설정
        (activity as AppCompatActivity).setSupportActionBar(toolbarHome)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarLayout.setOnClickListener{
            homeMapLauncher.launch(
                HomeMapActivity.newIntent(
                    requireContext()
                )
            )
        }

        homeRecyclerView.adapter = listAdapter

        binding.homeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        binding.fabHomeadd.setOnClickListener {
            val intent = HomeAddActivity.homeAddActivityNewIntent(this@HomeFragment.requireContext())
            startActivity(intent)
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