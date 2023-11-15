package com.wd.woodong2.retrofit

import com.wd.woodong2.data.remotesource.GCMRemoteSource
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GCMRetrofitClient {

    private const val BASE_URL = "https://fcm.googleapis.com/"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(GCMRequestInterceptor())
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val gcmRemoteSource: GCMRemoteSource by lazy {
        retrofit.create(GCMRemoteSource::class.java)
    }
}