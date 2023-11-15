package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName

data class HomeMapSearchResponse(
    @SerializedName("documents")
    val documents: List<Place>?
)

data class Place(
    @SerializedName("place_name")
    val placeName: String?,             // 장소명, 업체명
    @SerializedName("address_name")
    val addressName: String?,           // 전체 지번 주소
    @SerializedName("road_address_name")
    val roadAddressName: String?,       // 전체 도로명 주소
    @SerializedName("x")
    val x: Double?,                      // longitude
    @SerializedName("y")
    val y: Double?,                      // latitude
    @SerializedName("distance")
    val distance: Int?                  //반경
)