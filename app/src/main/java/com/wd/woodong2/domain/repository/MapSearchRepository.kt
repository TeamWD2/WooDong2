package com.wd.woodong2.domain.repository

import com.wd.woodong2.domain.model.MapSearchEntity
import com.wd.woodong2.domain.model.PlaceEntity

interface MapSearchRepository {
    suspend fun getSearchMap(
        query: String,
    ): MapSearchEntity

    suspend fun getCircumLocationSearchMap(
        y: Double,
        x: Double,
        radius: Int,
        query: String,
    ): MapSearchEntity
}