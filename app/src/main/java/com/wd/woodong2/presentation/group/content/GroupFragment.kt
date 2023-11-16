package com.wd.woodong2.presentation.group.content

import android.app.Activity
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.wd.woodong2.databinding.GroupFragmentBinding
import com.wd.woodong2.presentation.group.add.GroupAddActivity
import com.wd.woodong2.presentation.group.detail.GroupDetailActivity
import com.wd.woodong2.presentation.home.map.HomeMapActivity

class GroupFragment : Fragment() {
    companion object {
        fun newInstance() = GroupFragment()
    }

    private var _binding: GroupFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupViewModel by viewModels {
        GroupViewModelFactory(requireContext())
    }

    private val groupListAdapter by lazy {
        GroupListAdapter(
            itemClickListener = { item ->
                clickGroupItem(item)
            }
        )
    }

    private var keyword = ""

    private lateinit var homeMapLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = GroupFragmentBinding.inflate(inflater, container, false)

        homeMapLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val receivedDataFirstLocation =
                        result.data!!.getStringExtra(HomeMapActivity.EXTRA_FIRST_LOCATION)
                    val receivedDataSecondLocation =
                        result.data!!.getStringExtra(HomeMapActivity.EXTRA_SECOND_LOCATION)

                    binding.toolbarGroup.setToolbarTitleText(HomeMapActivity.extractLocationInfo(HomeMapActivity.extractLocationInfo(receivedDataFirstLocation.toString())))

                    // firebase에 있는 값을 변경
                    viewModel.updateUserLocation(
                        receivedDataFirstLocation.toString(),
                        receivedDataSecondLocation.toString()
                    )
                    //SharedPreference에 저장
                    viewModel.userInfo.value = viewModel.editPrefUserInfo(
                        viewModel.getUserInfo()?.name.toString(),
                        viewModel.getUserInfo()?.imgProfile.toString(),
                        receivedDataFirstLocation.toString(),
                        receivedDataSecondLocation.toString())
                } else {

                }
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = with(binding) {
        toolbarGroup.setToolbarTitleText(HomeMapActivity.extractLocationInfo(viewModel.getUserInfo()?.firstLocation.toString()))

        toolbarGroup.setLocationOnClickListener {
            homeMapLauncher.launch(
                HomeMapActivity.newIntent(
                    requireContext(), viewModel.getUserInfo()?.firstLocation.toString(), viewModel.getUserInfo()?.secondLocation.toString()
                )
            )
        }

        // 검색 아이콘 클릭 리스너
        toolbarGroup.setRightIcOnClickListener {
            toolbarGroup.setConstraintSearchVisible(true)
            toolbarGroup.toolbarBinding.edtToolbarSearch.addTextChangedListener(searchTextWatcher())
            toolbarGroup.setRequestFocusEditText()
            toolbarGroup.setRightIcVisible(false)
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(toolbarGroup.toolbarBinding.edtToolbarSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        // 검색창 취소 아이콘 클릭 리스너
        toolbarGroup.setCancelOnClickListener {
            toolbarGroup.setConstraintSearchVisible(false)
            toolbarGroup.setClearEditText()
            keyword = toolbarGroup.getSearchEditText()
            viewModel.setKeyword(keyword)
            toolbarGroup.setRightIcVisible(true)
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(toolbarGroup.toolbarBinding.edtToolbarSearch.windowToken, 0)
        }

        // Floating 버튼 클릭 리스너
        fabAddGroup.setOnClickListener {
            startActivity(GroupAddActivity.newIntent(requireContext()))
        }

        recyclerViewGroup.adapter = groupListAdapter
        viewModel.getGroupItem() //Firebase 통신 - 모임 데이터 가져오기
    }

    private fun initViewModel() = with(viewModel) {
        userInfo.observe(viewLifecycleOwner){ userInfo ->
            binding.toolbarGroup.setToolbarTitleText(HomeMapActivity.extractLocationInfo(viewModel.getUserInfo()?.firstLocation.toString()))
            //사용자 위치 변경시 초기 모임 설정
            _printList.value = groupList.value?.filter { item ->
                item is GroupItem.GroupMain && item.groupLocation?.contains(userInfo?.firstLocation.toString()) == true
            }

            if((printList.value?.size ?: 0) < 5){
                HomeMapActivity.getLocationFromAddress(
                    requireContext(),
                    userInfo?.firstLocation.toString()
                )
                circumLocationItemSearch(
                    HomeMapActivity.latitude,
                    HomeMapActivity.longitude,
                    20000,
                    userInfo?.firstLocation.toString(),
                    userInfo?.firstLocation.toString()
                )
            }
        }
        searchKeyword.observe(viewLifecycleOwner) { keyword ->
            groupListAdapter.submitList(searchKeywordGroupItem(keyword))
        }

        printList.observe(viewLifecycleOwner) {
            if(_printList.value.isNullOrEmpty().not()){
                groupListAdapter.submitList(searchKeywordGroupItem(keyword))
            }
        }

        groupList.observe(viewLifecycleOwner){
            userInfo.postValue(getUserInfo())
        }

        loadingState.observe(viewLifecycleOwner) { loadingState ->
            binding.progressBar.isVisible = loadingState
        }

        isEmptyList.observe(viewLifecycleOwner) { isEmptyList ->
            binding.txtEmptyGroupList.isVisible = isEmptyList
        }
    }

    private fun searchTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            keyword = binding.toolbarGroup.getSearchEditText()
            viewModel.setKeyword(keyword)
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