package com.wd.woodong2.presentation.home.detail

import android.content.Context
import android.content.Intent
import com.wd.woodong2.presentation.home.content.HomeItem

class HomeDetailActivity {

    companion object {
        private lateinit var HomeDetailItem: HomeItem
        fun homeDetailActivityNewIntent(context: Context?, homeItem: HomeItem) =
            Intent(context, HomeDetailActivity::class.java).apply {
                HomeDetailItem = homeItem
            }
    }

}