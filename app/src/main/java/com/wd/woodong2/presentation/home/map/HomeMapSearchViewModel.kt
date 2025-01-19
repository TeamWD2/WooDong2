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
    private val mapSearch : MapSearchGetItemsUseCase
) : ViewModel(){
    companion object {
        private const val TAG = "HomeMapSearchViewModel"
    }

    private val _list: MutableLiveData<List<HomeMapSearchItem>> = MutableLiveData()
    val list: LiveData<List<HomeMapSearchItem>> get() = _list

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    fun search(query: String) = viewModelScope.launch {
        _isLoading.value = true
        runCatching {
            val items = createItems(map = mapSearch(query))
            _list.postValue(items)
            _isSuccess.value = true
        }.onFailure { e ->
            if (e is retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("Retrofit Error", "$TAG Request failed: code(${e.code()}), message($errorBody)")
            } else {
                Log.e("Retrofit Error", "$TAG Request failed: ${e.message}")
            }
            _isSuccess.value = false
        }.also {
            _isLoading.value = false
        }
    }

    private fun createItems(
        map: MapSearchEntity
    ): List<HomeMapSearchItem> {
        fun createMapSearchItems(
            map: MapSearchEntity
        ): List<HomeMapSearchItem.MapSearchItem> = map.documents.map { document ->
            HomeMapSearchItem.MapSearchItem(
                address = document.addressName,
                x = document.x,
                y = document.y
            )
        }

        return createMapSearchItems(map)
    }
}