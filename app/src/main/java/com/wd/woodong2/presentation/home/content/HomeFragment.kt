package com.wd.woodong2.presentation.home.content


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wd.woodong2.databinding.HomeFragmentBinding
import com.wd.woodong2.presentation.home.add.HomeAddActivity
import com.wd.woodong2.presentation.home.detail.HomeDetailActivity
import com.wd.woodong2.presentation.home.map.HomeMapActivity
import com.wd.woodong2.presentation.home.map.HomeMapActivity.Companion.EXTRA_FIRST_LOCATION
import com.wd.woodong2.presentation.home.map.HomeMapActivity.Companion.EXTRA_SECOND_LOCATION


class HomeFragment : Fragment() {
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel
            by viewModels {
                HomeViewModelFactory(requireContext())
            }
    private var currentTag: String? = null
    private var firstLocation: String? = null
    private var secondLocation: String? = null
    private var userName: String? = null
    private var userId: String? = null
    private lateinit var homeMapLauncher: ActivityResultLauncher<Intent>

    private val listAdapter by lazy {
        HomeListAdapter(
            requireContext(),
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
                updateTagSelectionUI()
            },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)

        homeMapLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val receivedDataFirstLocation =
                        result.data!!.getStringExtra(EXTRA_FIRST_LOCATION)
                    val receivedDataSecondLocation =
                        result.data!!.getStringExtra(EXTRA_SECOND_LOCATION)
                    firstLocation = receivedDataFirstLocation
                    secondLocation = receivedDataSecondLocation
                    binding.toolbarHome.setToolbarTitleText(HomeMapActivity.extractLocationInfo(firstLocation.toString()))
                    // firebase에 있는 값을 변경
                    viewModel.updateUserLocation(
                        receivedDataFirstLocation.toString(),
                        receivedDataSecondLocation.toString()
                    )
                    //SharedPreference에 저장
                    viewModel.userInfo.value = viewModel.editPrefUserInfo(
                        viewModel.userInfo.value?.name,
                        viewModel.userInfo.value?.imgProfile,
                        receivedDataFirstLocation.toString(),
                        receivedDataSecondLocation.toString()
                    )
                    Log.d("check", viewModel.userInfo.value?.firstLocation.toString())
                } else {

                }
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()

        setupScrollToTopButton()
    }

    private fun initView() = with(binding) {
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbarHome.toolbarBinding.toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)

        homeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
        toolbarHome.setLocationOnClickListener {
            homeMapLauncher.launch(
                HomeMapActivity.newIntent(
                    requireContext(), firstLocation.toString(), secondLocation.toString()
                )
            )
        }
        fabHomeAdd.setOnClickListener {
            val intent = HomeAddActivity.homeAddActivityNewIntent(
                requireContext(),
                firstLocation.toString(), userName,
                userId.toString()
            )
            updateTagSelectionUI()
            startActivity(intent)
        }
        homeTag1.setOnClickListener {
            currentTag = if (currentTag == "All") null else "All"
            handleTagSelection(currentTag)
        }
        homeTag2.setOnClickListener {
            currentTag = if (currentTag == "동네질문") null else "동네질문"
            handleTagSelection(currentTag)
        }
        homeTag3.setOnClickListener {
            currentTag = if (currentTag == "조심해요!") null else "조심해요!"
            handleTagSelection(currentTag)
        }
        homeTag4.setOnClickListener {
            currentTag = if (currentTag == "정보공유") null else "정보공유"
            handleTagSelection(currentTag)
        }
        homeTag5.setOnClickListener {
            currentTag = if (currentTag == "동네소식") null else "동네소식"
            handleTagSelection(currentTag)
        }
        homeTag6.setOnClickListener {
            currentTag = if (currentTag == "사건/사고") null else "사건/사고"
            handleTagSelection(currentTag)
        }
        homeTag7.setOnClickListener {
            currentTag = if (currentTag == "동네사진전") null else "동네사진전"
            handleTagSelection(currentTag)
        }
        homeTag8.setOnClickListener {
            currentTag = if (currentTag == "분실/실종") null else "분실/실종"
            handleTagSelection(currentTag)
        }
        homeTag9.setOnClickListener {
            currentTag = if (currentTag == "생활정보") null else "생활정보"
            handleTagSelection(currentTag)
        }

        // 검색 필드 초기화
        toolbarHome.toolbarBinding.edtToolbarSearch.apply {
            // EditText가 줄바꿈 대신 검색을 수행하도록 설정
            inputType = InputType.TYPE_CLASS_TEXT
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    // 검색 수행
                    viewModel.searchItems(text.toString())
                    visibility = View.GONE
                    toolbarHome.setCancelIcVisible(false)
                    toolbarHome.setRightIcVisible(true)
                    toolbarHome.setCancelIcVisible(false)

                    // 키보드 숨기기
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(windowToken, 0)
                    true // 이벤트 처리 완료
                } else {
                    false // 기본 이벤트 처리를 계속 진행
                }
            }
        }

        toolbarHome.setRightIcOnClickListener {
            if (!toolbarHome.isVisibleConstraintSearch()) {
                // 검색 필드 보여주기
                toolbarHome.setConstraintSearchVisible(true)
                toolbarHome.setRequestFocusEditText()
                toolbarHome.setClearEditText() // 이전 검색어 지우기
                toolbarHome.setCancelIcVisible(true)

                // 키보드 보여주기
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(toolbarHome.toolbarBinding.edtToolbarSearch, InputMethodManager.SHOW_IMPLICIT)
            } else {
                // 검색 수행
                viewModel.searchItems(toolbarHome.getSearchEditText())
                toolbarHome.setConstraintSearchVisible(false)
                toolbarHome.setCancelIcVisible(false)
                toolbarHome.setRightIcVisible(true)

                // 키보드 숨기기
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(toolbarHome.toolbarBinding.edtToolbarSearch.windowToken, 0)
                toolbarHome.setClearEditText() // 검색 후 검색어 지우기
            }
        }

        toolbarHome.setCancelOnClickListener {
            toolbarHome.setConstraintSearchVisible(false)
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(toolbarHome.toolbarBinding.edtToolbarSearch.windowToken, 0)
        }

    }

    private fun initViewModel() {
        with(viewModel) {

            list.observe(viewLifecycleOwner) { newList ->
                _printList.value = newList.filter { item ->
                    circumLocation.contains(item.location)
                }
            }

            printList.observe(viewLifecycleOwner) {
                listAdapter.submitList(it)
            }

            userInfo.observe(viewLifecycleOwner) { userInfo ->
                val filteredList =
                    list.value?.filter { it.location == userInfo?.firstLocation } ?: emptyList()
                _printList.value = filteredList



                printList.value?.let { list ->
                    if (list.size < 10) {
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

                    handleTagSelection(currentTag)
                }


                firstLocation = userInfo?.firstLocation
                secondLocation = userInfo?.secondLocation
                userName = userInfo?.name
                userId = userInfo?.id.toString()
                binding.toolbarHome.setToolbarTitleText(HomeMapActivity.extractLocationInfo(firstLocation.toString()))
                if (userInfo?.firstLocation == "") {

                    Toast.makeText(requireContext(), "위치 설정이 필요합니다", Toast.LENGTH_SHORT).show()

                    homeMapLauncher.launch(
                        HomeMapActivity.newIntent(
                            requireContext(), firstLocation.toString(), secondLocation.toString()
                        )
                    )
                }


            }

            searchResults.observe(viewLifecycleOwner) { results ->
                listAdapter.submitList(results)
            }

            filteredItems.observe(viewLifecycleOwner) { updatedList ->
                listAdapter.submitList(updatedList)
            }
        }
    }
    private fun updateTagSelectionUI() {
        binding.homeTagGroup.clearCheck() // 칩 그룹 내 모든 칩의 선택 해제
    }

    private fun handleTagSelection(tag: String?) {
        if (tag == null) {
            // 태그가 해제되었을 때의 로직
            viewModel.resetToInitialList()
        } else {
            // 태그가 선택되었을 때의 로직
            viewModel.tagFilterList(tag)
        }
    }

    private fun setupScrollToTopButton() {
        val fab: FloatingActionButton = binding.homeTopFab
        fab.hide() // 초기 상태에서 FAB 숨기기
        fab.setOnClickListener {
            binding.homeRecyclerView.scrollToPosition(0) // 리사이클러뷰의 맨 위로 스크롤
        }
        binding.homeRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!fab.isShown) {
                    fab.show()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.computeVerticalScrollOffset() == 0) {
                    fab.hide()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}