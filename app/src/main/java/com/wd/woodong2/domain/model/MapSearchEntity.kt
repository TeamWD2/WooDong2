package com.wd.woodong2.domain.model

import com.google.gson.annotations.SerializedName

data class MapSearchEntity(
    @SerializedName("documents")
    var documents: List<PlaceEntity>
)
data class PlaceEntity(
    var placeName: String,             // 장소명, 업체명
    var addressName: String,           // 전체 지번 주소
    var roadAddressName: String,      // 전체 도로명 주소
    var x: Double,                      // longitude
    var y: Double,                      // latitude
)