package com.wd.woodong2.presentation.home.map

data class AddressSearchResponse (
    var documents: List<Place>
)

data class Place(
    var place_name: String,             // 장소명, 업체명
    var address_name: String,           // 전체 지번 주소
    var road_address_name: String,      // 전체 도로명 주소
    var x: String,                      // longitude
    var y: String,                      // latitude
)