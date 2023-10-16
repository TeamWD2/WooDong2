package com.wd.woodong2.presentation.home.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager

import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeMapSearchActivityBinding


class HomeMapSearchActivity : AppCompatActivity() {
    companion object {
        //private lateinit var HomeMapItem: HomeItem
        fun newIntent(context: Context)=//, homeItem: HomeItem) =
        Intent(context, HomeMapSearchActivity::class.java).apply {
                //HomeMapItem = homeItem
            }
    }

    private lateinit var binding : HomeMapSearchActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomeMapSearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(
            R.anim.home_map_search_right,
            R.anim.home_map_none_fragment
        )

        initView()
    }
    private fun initView(){
        binding.homeMapSearchClose.setOnClickListener{
            finish()
            overridePendingTransition(
                R.anim.home_map_none_fragment,
                R.anim.home_map_search_left
            )
        }
        binding.etSearch.setOnClickListener {
            hideKeyboard()
        }
    }
    private fun hideKeyboard() {
        val view = this.currentFocus
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}