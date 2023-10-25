package com.wd.woodong2.data.repository

import com.wd.woodong2.domain.model.MapSearchEntity
import com.wd.woodong2.domain.model.PlaceEntity
import com.wd.woodong2.domain.model.toPlaceEntity
import com.wd.woodong2.domain.repository.MapSearchRepository
import com.wd.woodong2.presentation.home.map.HomeMapSearchRemoteDatasource

class MapSearchRepositoryImpl (
    private val homeMapSearchRemoteDatasource: HomeMapSearchRemoteDatasource
) : MapSearchRepository{

    override suspend fun getSearchMap(
        query: String
    ) :MapSearchEntity = homeMapSearchRemoteDatasource.getAddressSearch(
            query
        ).toPlaceEntity()
}