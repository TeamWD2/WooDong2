package com.wd.woodong2.retrofit

import com.wd.woodong2.R
import com.wd.woodong2.WooDongApp
import okhttp3.Interceptor
import okhttp3.Response

class KAKAORequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader(
                "Request",
                "KakaoAK %s".format(
                    WooDongApp.getApp().getString(R.string.home_map_search_kakao_api)
                )
            ).build()
        return chain.proceed(newRequest)
    }
}