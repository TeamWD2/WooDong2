package com.wd.woodong2.presentation.home.content


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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
    private val viewModel : HomeViewModel //= HomeViewModelFactory().create(HomeViewModel::class.java)
        by activityViewModels {
            HomeViewModelFactory()
        }

    private var firstLocation :String? = null
    private var secondLocation :String? = null
    private var userName :String? = null
    private var homeItemCount : Int? = 0
    private lateinit var homeMapLauncher : ActivityResultLauncher<Intent>
    private var count : Int? = 1
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

        homeMapLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val receivedDataFirstLocation = result.data!!.getStringExtra(EXTRA_FIRSTLOCATION)
                    val receivedDataSecondLocation = result.data!!.getStringExtra(EXTRA_SECONDLOCATION)
                    firstLocation = receivedDataFirstLocation
                    secondLocation = receivedDataSecondLocation
                    binding.toolbarTvLocation.text = HomeMapActivity.extractLocationInfo(firstLocation.toString())
                    // firebase에 있는 값을 변경
                    viewModel.updateUserLocation(receivedDataFirstLocation.toString(), receivedDataSecondLocation.toString())
                } else {

                }
            }

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


        (activity as? AppCompatActivity)?.setSupportActionBar(toolbarHome)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)

        homeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
        toolbarLayout.setOnClickListener{
            homeMapLauncher.launch(
                HomeMapActivity.newIntent(
                    requireContext(), firstLocation.toString(), secondLocation.toString()
                )
            )
        }
        fabHomeadd.setOnClickListener {
            // 과연 주소를 받아 올까 그게 문제야
            count = count!! + 5
            HomeMapActivity.getLocationFromAddress(requireContext(), firstLocation.toString())
            viewModel.circumLocationItemSearch(
                HomeMapActivity.latitude,
                HomeMapActivity.longitude,
                1000* count!!,
                firstLocation.toString()
            )
            val intent = HomeAddActivity.homeAddActivityNewIntent(requireContext(),
                firstLocation.toString(),userName
            )
            startActivity(intent)
        }
    }
    private fun initViewModel(){
        with(viewModel){
            list.observe(viewLifecycleOwner){
                listAdapter.submitList(it)
                homeItemCount = list.value?.size
                if(homeItemCount!! < 10){
//
                }
            }

            userInfo.observe(viewLifecycleOwner){userInfo->
                firstLocation = userInfo.firstLocation
                secondLocation = userInfo.secondLocation
                userName = userInfo.name
                binding.toolbarTvLocation.text = HomeMapActivity.extractLocationInfo(firstLocation.toString())
                if(userInfo.firstLocation == ""){

                    Toast.makeText(requireContext(), "위치 설정이 필요합니다", Toast.LENGTH_SHORT).show()

                    homeMapLauncher.launch(
                        HomeMapActivity.newIntent(
                            requireContext(), firstLocation.toString(), secondLocation.toString()
                        )
                    )
                }
            }
        }
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}