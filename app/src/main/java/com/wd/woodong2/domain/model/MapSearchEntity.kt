package com.wd.woodong2.domain.model

data class MapSearchEntity(
    val documents: List<PlaceEntity>
)

data class PlaceEntity(
    val placeName: String,             // 장소명, 업체명
    val addressName: String,           // 전체 지번 주소
    val roadAddressName: String,      // 전체 도로명 주소
    val x: Double,                      // longitude
    val y: Double,                      // latitude
    val distance: Int
)