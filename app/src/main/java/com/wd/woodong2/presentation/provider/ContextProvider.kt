package com.wd.woodong2.presentation.provider

import android.content.SharedPreferences
import androidx.annotation.StringRes

interface ContextProvider {
    fun getSharedPreferences(name: String): SharedPreferences
    fun getString(@StringRes stringRes: Int): String
}