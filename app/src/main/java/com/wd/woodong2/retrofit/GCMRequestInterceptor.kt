package com.wd.woodong2.retrofit

import android.util.Log
import com.wd.woodong2.BuildConfig
import com.wd.woodong2.R
import com.wd.woodong2.WooDongApp
import okhttp3.Interceptor
import okhttp3.Response

class GCMRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader(
                "Authorization", "key=%s".format(
                    BuildConfig.GCM_API_KEY
                )
            )
            .addHeader("Content-Type", "application/json")
            .build()

        return chain.proceed(newRequest)
    }
}
