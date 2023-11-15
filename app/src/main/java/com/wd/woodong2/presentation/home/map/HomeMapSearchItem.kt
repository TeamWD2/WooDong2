package com.wd.woodong2.presentation.home.map
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class HomeMapSearchItem{
    data class MapSearchItem(
        val address: String?,        // 지번 주소
        val x: Double = 0.0,         // 경도(Longitude)
        val y: Double = 0.0         // 위도(Latitude)
    ): HomeMapSearchItem()
}
