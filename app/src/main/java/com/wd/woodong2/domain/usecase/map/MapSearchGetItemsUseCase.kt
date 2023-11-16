package com.wd.woodong2.domain.usecase.map

import com.wd.woodong2.domain.model.MapSearchEntity
import com.wd.woodong2.domain.model.PlaceEntity
import com.wd.woodong2.domain.repository.MapSearchRepository

class MapSearchGetItemsUseCase (
    private val repository: MapSearchRepository
){
    suspend operator fun invoke(
        query: String
    ):MapSearchEntity = repository.getSearchMap(
        query
    )

}