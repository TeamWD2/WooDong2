package com.wd.woodong2.retrofit

import android.util.Log
import com.wd.woodong2.R
import com.wd.woodong2.WooDongApp
import okhttp3.Interceptor
import okhttp3.Response

class GCMRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader(
                "Authorization", "key=%s".format(
                    WooDongApp.getApp().getString(R.string.gcm_api)
                )
            )
            .addHeader("Content-Type", "application/json")
            .build()

        Log.d("GCMRequestInterceptor", newRequest.toString())

        return chain.proceed(newRequest)
    }
}
