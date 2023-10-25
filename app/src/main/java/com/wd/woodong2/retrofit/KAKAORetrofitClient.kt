package com.wd.woodong2.retrofit

import com.wd.woodong2.presentation.home.map.HomeMapSearchRemoteDatasource
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KAKAORetrofitClient {

    private const val BASE_URL = "https://dapi.kakao.com"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(KAKAORequestInterceptor())
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val search: HomeMapSearchRemoteDatasource by lazy {
        retrofit.create(HomeMapSearchRemoteDatasource::class.java)
    }
}