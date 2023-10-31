package com.wd.woodong2.presentation.provider

import android.content.Context
import android.content.SharedPreferences

class ContextProviderImpl(
    private val context: Context,
) : ContextProvider {

    override fun getSharedPreferences(name: String): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    override fun getString(stringRes: Int): String {
        return context.getString(stringRes)
    }
}