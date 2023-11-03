package com.wd.woodong2.presentation.home.map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
    private val listAdapter : HomeMapSearchListAdapter by lazy{
        HomeMapSearchListAdapter(
            onClickItem = { _, item ->
                if(item.address != firstLocation && item.address != secondLocation){
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
        setContentView(binding.root)
        overridePendingTransition(
            R.anim.home_map_search_right,
            R.anim.home_map_none_fragment
        )

        initView()
        initViewModel()
    }
    private fun initView(){
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

        binding.homeMapEtSearch.setOnClickListener {
//            hideKeyboard()
        }

        binding.homeMapSearchBtn.setOnClickListener{
            address = binding.homeMapEtSearch.text.toString()
            if (address.endsWith("동") || address.endsWith("읍") || address.endsWith("면")) {
                viewModel.search(address)
            }
            else{
                Toast.makeText(this, R.string.home_map_search_error_btn, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun initViewModel() {
        viewModel.list.observe(this) {
            listAdapter.submitList(it)
        }
    }

//    private fun hideKeyboard() {
//        val view = this.currentFocus
//        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
//    }


}