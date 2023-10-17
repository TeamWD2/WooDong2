package com.wd.woodong2.presentation.home.map

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAPI {
    @GET("v2/local/search/keyword.json")
    fun getAddressSearch(
        @Header("Authorization") key: String,
        @Query("query") query: String

    ): Call<AddressSearchResponse>
}