package com.wd.woodong2.presentation.home.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wd.woodong2.domain.model.MapSearchEntity
import com.wd.woodong2.domain.usecase.map.MapSearchGetItemsUseCase
import kotlinx.coroutines.launch

class HomeMapSearchViewModel (
    private val MapSearch : MapSearchGetItemsUseCase
) : ViewModel(){
    private val _list: MutableLiveData<List<HomeMapSearchItem>> = MutableLiveData()
    val list: LiveData<List<HomeMapSearchItem>> get() = _list

    fun search(
        query: String
    ) = viewModelScope.launch {
        runCatching {
            val items = createItems(
                Map = MapSearch(query)
            )

            _list.postValue(items)
        }.onFailure { e ->
            Log.e("Retrofit Error", "Request failed: ${e.message}")
        }
    }

    private fun createItems(
        Map: MapSearchEntity
    ): List<HomeMapSearchItem> {
        fun createMapSearchItems(
            Map: MapSearchEntity
        ): List<HomeMapSearchItem.MapSearchItem> = Map.documents.map { document ->
            HomeMapSearchItem.MapSearchItem(
                address = document.addressName,
                x = document.x,
                y = document.y
            )
        }

        return createMapSearchItems(Map)
    }
}