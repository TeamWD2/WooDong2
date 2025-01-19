package com.wd.woodong2.retrofit

import com.wd.woodong2.BuildConfig
import com.wd.woodong2.R
import com.wd.woodong2.WooDongApp
import okhttp3.Interceptor
import okhttp3.Response

class KAKAORequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader(
                "Authorization",
                "KakaoAK %s".format(BuildConfig.KAKAO_API_KEY)
            ).build()
        return chain.proceed(newRequest)
    }
}