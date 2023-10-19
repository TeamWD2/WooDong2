package com.wd.woodong2.presentation.home.map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeMapSearchActivityBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeMapSearchActivity : AppCompatActivity() {

    companion object {

        //private lateinit var HomeMapItem: HomeItem
        fun newIntent(context: Context)=//, homeItem: HomeItem) =
        Intent(context, HomeMapSearchActivity::class.java).apply {
                //HomeMapItem = homeItem
            }

    }

    private lateinit var binding : HomeMapSearchActivityBinding
    //private val listItems = arrayListOf<HomeMapSearchItem.MapSearchItem>()
    private val viewModel : HomeMapSearchViewModel by viewModels{
        HomeMapSearchViewModelFactory()
    }
    private val listAdapter : HomeMapSearchListAdapter by lazy{
        HomeMapSearchListAdapter(
            onClickItem = { _, item ->

            val intent = Intent().apply{
                putExtra(
                    "Address",
                    item.address
                )
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
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
            finish()
            overridePendingTransition(
                R.anim.home_map_none_fragment,
                R.anim.home_map_search_left
            )
        }
        binding.homeMapEtSearch.setOnClickListener {
            hideKeyboard()
        }
        binding.homeMapSearchBtn.setOnClickListener{
            address = binding.homeMapEtSearch.text.toString()
            viewModel.search(address)
        }
    }
    private fun initViewModel() {
        viewModel.list.observe(this) {
            listAdapter.submitList(it)
        }
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }


}