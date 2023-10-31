package com.wd.woodong2.presentation.provider

import android.content.SharedPreferences

interface ContextProvider {
    fun getSharedPreferences(name: String): SharedPreferences
}