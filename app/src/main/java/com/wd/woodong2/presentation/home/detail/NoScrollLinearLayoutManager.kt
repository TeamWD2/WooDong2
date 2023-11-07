package com.wd.woodong2.presentation.home.detail

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager


class NoScrollLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun canScrollVertically(): Boolean {
        return false
    }

    override fun canScrollHorizontally(): Boolean {
        return false
    }
}