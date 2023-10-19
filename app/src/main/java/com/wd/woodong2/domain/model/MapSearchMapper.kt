package com.wd.woodong2.domain.model

import com.wd.woodong2.data.model.HomeMapSearchResponse
import com.wd.woodong2.data.model.Place
fun HomeMapSearchResponse.toPlaceEntity() = MapSearchEntity(
    documents = documents!!.map { response ->
        response.toEntity()
    }
)

fun Place.toEntity() = PlaceEntity(
    placeName = placeName!!,             // 장소명, 업체명
    addressName = addressName!!,                  // 전체 지번 주소
    roadAddressName = roadAddressName!!,          // 전체 도로명 주소
    x = x!!,                                 // longitude
    y = y!!                                 // latitude
)