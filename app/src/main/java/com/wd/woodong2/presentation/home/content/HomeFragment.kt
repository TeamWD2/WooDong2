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
import com.wd.woodong2.presentation.home.map.HomeMapActivity.Companion.EXTRA_FIRSTLOCATION
import com.wd.woodong2.presentation.home.map.HomeMapActivity.Companion.EXTRA_SECONDLOCATION


class HomeFragment : Fragment() {
    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _binding : HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel : HomeViewModel by viewModels {
        HomeViewModelFactory()
    }

    private var firstLocation :String? = null
    private var secondLocation :String? = null

    private val homeMapLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val receivedDataFirstLocation = result.data!!.getStringExtra(EXTRA_FIRSTLOCATION)
                val receivedDataSecondLocation = result.data!!.getStringExtra(EXTRA_SECONDLOCATION)
                firstLocation = receivedDataFirstLocation
                secondLocation = receivedDataSecondLocation
                // firebase에 있는 값을 변경
                binding.toolbarTvLocation.text = firstLocation
                updateLocationData(firstLocation,secondLocation)
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
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbarHome)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarLayout.setOnClickListener{
            homeMapLauncher.launch(
                HomeMapActivity.newIntent(
                    requireContext(), firstLocation.toString(), secondLocation.toString()
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
            userInfo.observe(viewLifecycleOwner){userInfo->
                firstLocation = userInfo.firstLocation
                secondLocation = userInfo.secondLocation
                binding.toolbarTvLocation.text = firstLocation
            }
        }
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    private fun updateLocationData(firstLocation:String?, secondLocation:String?){
        //firebase에 값을 갱신하는     -> 뷰모델로 가야한다

    }
}