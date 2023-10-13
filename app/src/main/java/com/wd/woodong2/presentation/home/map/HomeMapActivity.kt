package com.wd.woodong2.presentation.home.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeMapActivityBinding
import com.wd.woodong2.presentation.home.content.HomeItem
import com.wd.woodong2.presentation.home.detail.HomeDetailActivity


class HomeMapActivity : AppCompatActivity() {
    companion object {
        //private lateinit var HomeMapItem: HomeItem
        fun homeMapActivityNewIntent(context: Context?)=//, homeItem: HomeItem) =
            Intent(context, HomeMapActivity::class.java).apply {

                //HomeMapItem = homeItem
            }
    }

    private lateinit var binding : HomeMapActivityBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeMapActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(
            R.anim.home_map_slide_up_fragment,
            R.anim.home_map_none_fragment
        )
        initView()
    }
    private fun initView(){
        binding.homeMapClose.setOnClickListener{
            finish()
            overridePendingTransition(
                R.anim.home_map_none_fragment,
                R.anim.home_map_slide_down_fragment
            )
        }
    }

}