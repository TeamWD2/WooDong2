package com.wd.woodong2.domain.usecase

import com.wd.woodong2.domain.model.MapSearchEntity
import com.wd.woodong2.domain.repository.MapSearchRepository

class MapSearchCircumLocationGetItemsUseCase (
    private val repository: MapSearchRepository
){
    suspend operator fun invoke(
        y: Double,
        x: Double,
        radius: Int,
        query: String,
    ): MapSearchEntity = repository.getCircumLocationSearchMap(
        y,
        x,
        radius,
        query,
    )

}