package com.wd.woodong2.presentation.home.map

import com.wd.woodong2.data.model.HomeMapSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface HomeMapSearchRemoteDatasource {
    @GET("v2/local/search/keyword.json")
    suspend fun getAddressSearch(
        @Query("query") query: String
    ): HomeMapSearchResponse
}