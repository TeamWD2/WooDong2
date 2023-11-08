package com.wd.woodong2.presentation.home.map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsetsController
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeMapSearchActivityBinding



class HomeMapSearchActivity : AppCompatActivity() {

    companion object {
        private var firstLocation : String? ="Unknown Location"
        private var secondLocation : String? ="Unknown Location"

        const val EXTRA_ADDRESS = "extra_address"
        fun newIntent(context: Context, firstLoc: String, secondLoc:String)=
        Intent(context, HomeMapSearchActivity::class.java).apply {
            firstLocation = firstLoc
            secondLocation = secondLoc
            }

    }

    private lateinit var binding : HomeMapSearchActivityBinding
    private val viewModel : HomeMapSearchViewModel by viewModels{
        HomeMapSearchViewModelFactory()
    }

    private val onBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(firstLocation!!.isNotEmpty()) {
                finish()
                overridePendingTransition(
                    R.anim.home_map_none_fragment,
                    R.anim.home_map_search_left
                )
            }
            else{
                Toast.makeText(this@HomeMapSearchActivity, R.string.home_map_search_location_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val listAdapter : HomeMapSearchListAdapter by lazy{
        HomeMapSearchListAdapter(
            onClickItem = { _, item ->
                if(HomeMapActivity.extractLocationSetInfo(item.address.toString()) != HomeMapActivity.extractLocationSetInfo(firstLocation.toString()) && HomeMapActivity.extractLocationSetInfo(item.address.toString()) != HomeMapActivity.extractLocationSetInfo(secondLocation.toString())){
                    val intent = Intent().apply{
                        putExtra(
                            EXTRA_ADDRESS,
                            item.address
                        )
                    }

                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                else{
                    Toast.makeText(this, R.string.home_map_search_location_also_error, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    private var address = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomeMapSearchActivityBinding.inflate(layoutInflater)


        //상태바 & 아이콘 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.egg_yellow_toolbar)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 안드로이드 11 이상에서만 동작
//            window.insetsController?.setSystemBarsAppearance(
//                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
//                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
//            )
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 안드로이드 6.0 이상에서만 동작
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        } // 안드로이드 6.0 이하는 상태바 아이콘 색상 변경 지원 안함

        setContentView(binding.root)
        overridePendingTransition(
            R.anim.home_map_search_right,
            R.anim.home_map_none_fragment
        )

        initView()
        initViewModel()
    }
    private fun initView(){

        onBackPressedDispatcher.addCallback(this@HomeMapSearchActivity, onBackPressedCallback)

        binding.homeMapSearchRc.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.homeMapSearchRc.adapter = listAdapter


        binding.homeMapSearchClose.setOnClickListener{
            if(firstLocation!!.isNotEmpty()) {
                finish()
                overridePendingTransition(
                    R.anim.home_map_none_fragment,
                    R.anim.home_map_search_left
                )
            }
            else{
                Toast.makeText(this, R.string.home_map_search_location_error, Toast.LENGTH_SHORT).show()
            }
        }


        binding.homeMapSearchBtn.setOnClickListener{
            address = binding.homeMapEtSearch.text.toString()
            val spaceAddress = address.replace("\\s".toRegex(), "")
            if (address.endsWith("동") || address.endsWith("읍") || address.endsWith("면")) {
                viewModel.search(spaceAddress)
            }
            else{
                Toast.makeText(this, R.string.home_map_search_error_btn, Toast.LENGTH_SHORT).show()
            }
            hideKeyboard()
        }
        binding.homeMapEtSearch.setOnEditorActionListener{ v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.homeMapSearchBtn.performClick()

                handled = true
            }
            handled
        }

    }
    private fun initViewModel() {
        viewModel.list.observe(this) {
            listAdapter.submitList(it)
        }
    }
    // 화면 터치 시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }
    private fun hideKeyboard() {
        val view = this.currentFocus
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }


}