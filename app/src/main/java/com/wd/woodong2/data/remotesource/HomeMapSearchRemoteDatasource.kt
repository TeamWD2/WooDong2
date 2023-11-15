package com.wd.woodong2.data.remotesource

import com.wd.woodong2.data.model.HomeMapSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface HomeMapSearchRemoteDatasource {
    @GET("v2/local/search/address.json")
    suspend fun getAddressSearch(
        @Query("query") query: String
    ): HomeMapSearchResponse
    @GET("v2/local/search/keyword.json")
    suspend fun getCircumLocationAddressSearch(
        @Query("y") y: Double,
        @Query("x") x: Double,
        @Query("radius") radius: Int,
        @Query("query") query: String
    ): HomeMapSearchResponse
}