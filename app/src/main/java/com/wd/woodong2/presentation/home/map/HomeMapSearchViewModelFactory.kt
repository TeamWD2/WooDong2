package com.wd.woodong2.presentation.home.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wd.woodong2.data.repository.MapSearchRepositoryImpl
import com.wd.woodong2.domain.repository.MapSearchRepository
import com.wd.woodong2.domain.usecase.MapSearchGetItemsUseCase
import com.wd.woodong2.retrofit.KAKAORetrofitClient

class HomeMapSearchViewModelFactory : ViewModelProvider.Factory {
    private val repository : MapSearchRepository = MapSearchRepositoryImpl(
        KAKAORetrofitClient.search
    )
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeMapSearchViewModel::class.java)) {
            return HomeMapSearchViewModel(
                MapSearchGetItemsUseCase(repository)
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}