package com.wd.woodong2.data.repository

import com.wd.woodong2.domain.model.MapSearchEntity
import com.wd.woodong2.domain.model.toPlaceEntity
import com.wd.woodong2.domain.repository.MapSearchRepository
import com.wd.woodong2.data.remotesource.HomeMapSearchRemoteDatasource

class MapSearchRepositoryImpl (
    private val homeMapSearchRemoteDatasource: HomeMapSearchRemoteDatasource
) : MapSearchRepository{

    override suspend fun getSearchMap(
        query: String
    ) :MapSearchEntity = homeMapSearchRemoteDatasource.getAddressSearch(
            query
        ).toPlaceEntity()
    override suspend fun getCircumLocationSearchMap(
        y: Double,
        x: Double,
        radius: Int,
        query: String,
    ) : MapSearchEntity = homeMapSearchRemoteDatasource.getCircumLocationAddressSearch(
        y,
        x,
        radius,
        query,
    ).toPlaceEntity()
}