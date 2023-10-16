package com.wd.woodong2.presentation.home.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeMapActivityBinding
import java.util.Locale


class HomeMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        //private lateinit var HomeMapItem: HomeItem
        fun newIntent(context: Context?)=//, homeItem: HomeItem) =
            Intent(context, HomeMapActivity::class.java).apply {
                //HomeMapItem = homeItem
            }

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private var firstLocation : String? =null
    private var secondLocation : String? =null

    private lateinit var binding : HomeMapActivityBinding
    private lateinit var naverMap: NaverMap
    private val marker = Marker()
    private lateinit var locationSource: FusedLocationSource
    private val PERMISSIONS = arrayOf(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION
    )

    private val clientId = "kdzwicc7da"

    private var check : String? = null
    private var reverseCheck : LatLng? = null

    //    private lateinit var fusedLocationClient: FusedLocationProviderClient         // 사용자 실시간 위치 반영때 사용됨

    private val homeMapSearchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if(result.resultCode == Acitivty.RESULT_OK){
//
//            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeMapActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(
            R.anim.home_map_slide_up_fragment,
            R.anim.home_map_none_fragment
        )

        if (isPermitted()) {
            initHomeMapView()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

    }


    private fun initHomeMapView(){

        binding.homeMapClose.setOnClickListener{
            finish()
            overridePendingTransition(
                R.anim.home_map_none_fragment,
                R.anim.home_map_slide_down_fragment
            )
        }

        binding.homeMapSecondBtn.setOnClickListener{
            homeMapSearchLauncher.launch(
                HomeMapSearchActivity.newIntent(this@HomeMapActivity)
            )
        }

        //NAVER 지도 API 호출 및 ID 지정
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(clientId)

        //NAVER 객체 얻기 ( 동적 )
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.home_map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.home_map_view, it).commit()
            }

        //인터페이스 객체
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, Companion.LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun isPermitted(): Boolean {
        for (perm in PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
                finish()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        // 임의로 위치 설정 초기화
        var latitude: Double //= 37.123456
        var longitude: Double //= 127.654321

        //현재 위치 초기화
        naverMap.locationSource = locationSource
        naverMap.uiSettings.isLocationButtonEnabled = true

        //위치 추적
        //naverMap.locationTrackingMode = LocationTrackingMode.Follow

        //특정 위치로 이동
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(LatLng(37.5666102, 126.9783881), 15.0)

        //위치 변경 확인
        naverMap.addOnLocationChangeListener { location ->

            //위치 변경
            latitude = location.latitude
            longitude = location.longitude

            makeText(this, "${latitude}, $longitude",
                Toast.LENGTH_SHORT).show()
            Log.d(TAG, "${latitude}, $longitude")

        }
        binding.homeMapFirstBtn.setOnClickListener{

            check = getAddressFromLocation(naverMap.cameraPosition.target.latitude,
                naverMap.cameraPosition.target.longitude)
            makeText(this, "${check}, $check",
                Toast.LENGTH_SHORT).show()

            reverseCheck = getLocationFromAddress(this, "군자동")//check!!)
            makeText(this, "${reverseCheck}, $reverseCheck",
                Toast.LENGTH_SHORT).show()

        }


        naverMap.setOnMapClickListener { point, coord ->
            marker(coord.latitude, coord.longitude)
        }


    }
    private fun marker(latitude: Double, longitude: Double) {
        marker.position = LatLng(latitude, longitude)
        marker.map = naverMap

        getAddressFromLocation(latitude, longitude)
    }

    // 좌표 -> 주소 변환
    private fun getAddressFromLocation(lat: Double, lng: Double): String {
        return runCatching {
            val geoCoder = Geocoder(this, Locale.KOREA)
            val address: MutableList<Address>? = geoCoder.getFromLocation(lat, lng, 1)
            if (address!!.isNotEmpty()) {
                return address[0].getAddressLine(0).toString()
            } else {
                return "주소를 가져 올 수 없습니다."
            }
        }.getOrElse {
            it.printStackTrace()
            "주소를 가져 오는 도중 오류가 발생했습니다."
        }
    }

    //주소-> 좌표
    private fun getLocationFromAddress(context: Context, addressStr: String): LatLng? {
        return runCatching {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(addressStr, 1)

            if (addresses!!.isNotEmpty()) {
                val latitude = addresses[0].latitude
                val longitude = addresses[0].longitude
                return LatLng(latitude, longitude)
            } else {
                return null
            }
        }.getOrElse {
            it.printStackTrace()
            null
        }
    }
}