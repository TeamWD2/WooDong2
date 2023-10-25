package com.wd.woodong2.domain.model

import com.wd.woodong2.data.model.HomeMapSearchResponse
import com.wd.woodong2.data.model.Place

fun HomeMapSearchResponse.toPlaceEntity() = MapSearchEntity(
    documents = documents!!.map { response ->
        response.toEntity()
    }
)

fun Place.toEntity() = PlaceEntity(
    placeName = placeName.orEmpty(),             // 장소명, 업체명
    addressName = addressName.orEmpty(),                  // 전체 지번 주소
    roadAddressName = roadAddressName.orEmpty(),          // 전체 도로명 주소
    x = x ?: 0.0,                                 // longitude
    y = y ?: 0.0                               // latitude
)