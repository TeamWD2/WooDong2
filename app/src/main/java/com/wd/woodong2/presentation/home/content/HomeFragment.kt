package com.wd.woodong2.presentation.home.content


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wd.woodong2.R
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

    private var keyword = ""

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
                    binding.toolbarHome.setToolbarTitleText(
                        HomeMapActivity.extractLocationInfo(
                            firstLocation.toString()
                        )
                    )
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

        // 검색 아이콘 클릭 리스너
        toolbarHome.setRightIcOnClickListener {
            toolbarHome.setConstraintSearchVisible(true)
            toolbarHome.toolbarBinding.edtToolbarSearch.addTextChangedListener(searchTextWatcher())
            toolbarHome.setRequestFocusEditText()
            toolbarHome.setRightIcVisible(false)
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(
                toolbarHome.toolbarBinding.edtToolbarSearch,
                InputMethodManager.SHOW_IMPLICIT
            )
        }

        // 검색창 취소 아이콘 클릭 리스너
        toolbarHome.setCancelOnClickListener {
            toolbarHome.setConstraintSearchVisible(false)
            toolbarHome.setClearEditText()
            keyword = toolbarHome.getSearchEditText()
            viewModel.setKeyword(keyword)
            toolbarHome.setRightIcVisible(true)
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(toolbarHome.toolbarBinding.edtToolbarSearch.windowToken, 0)
        }
    }

    private fun initViewModel() = with(viewModel) {
        printList.observe(viewLifecycleOwner) {
            listAdapter.submitList(it)
        }

        userInfo.observe(viewLifecycleOwner) { userInfo ->
            _printList.value =
                list.value?.filter { it.location == userInfo?.firstLocation } ?: emptyList()

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
            binding.toolbarHome.setToolbarTitleText(
                HomeMapActivity.extractLocationInfo(
                    firstLocation.toString()
                )
            )
            if (userInfo?.firstLocation == "") {
                showHomeMapDialog()
            }
        }

        searchKeyword.observe(viewLifecycleOwner) { keyword ->
            listAdapter.submitList(searchItems(keyword))
        }

        filteredItems.observe(viewLifecycleOwner) { updatedList ->
            listAdapter.submitList(updatedList)
        }
    }

    private fun showHomeMapDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.home_dialog_no_location_title)
            setMessage(R.string.home_dialog_no_location_message)
            setPositiveButton(R.string.public_dialog_ok) { _, _ ->
                homeMapLauncher.launch(
                    HomeMapActivity.newIntent(
                        requireContext(),
                        firstLocation.toString(),
                        secondLocation.toString()
                    )
                )
            }
            setNegativeButton(R.string.public_dialog_cancel) { _, _ ->
                requireActivity().finish()
            }
            setCancelable(false) //dialog 외부 영역 클릭 시 창 꺼짐 현상 방지
            show()
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

    private fun searchTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            keyword = binding.toolbarHome.getSearchEditText()
            viewModel.setKeyword(keyword)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}