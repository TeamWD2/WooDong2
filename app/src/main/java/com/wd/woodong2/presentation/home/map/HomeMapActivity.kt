package com.wd.woodong2.presentation.home.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.load
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeMapActivityBinding
import java.util.Locale


class HomeMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val EXTRA_FIRSTLOCATION = "extra_firstlocation"
        const val EXTRA_SECONDLOCATION = "extra_secondlocation"

        private var firstLocation : String? ="Unknown Location"
        private var secondLocation : String? ="Unknown Location"
        fun newIntent(context: Context,firstLoc: String, secondLoc:String)=
            Intent(context, HomeMapActivity::class.java).apply {
                firstLocation = firstLoc
                secondLocation = secondLoc
            }
        // 동, 읍, 면 추출하기
        fun extractLocationInfo(address: String): String {
            val parts = address.split(" ")
            for (part in parts) {
                if (part.contains("동") || part.contains("읍") || part.contains("면")) {
                    return part
                }
            }
            return ""
        }

        private const val LOCATION_PERMISSION_REQUEST_CODE = 5000
    }

    private var clientId : String? = null
    private lateinit var binding : HomeMapActivityBinding
    private lateinit var naverMap: NaverMap
    private val marker = Marker()
    //기기 위치 추적
    private lateinit var locationSource: FusedLocationSource
    //권한 확인
    private val PERMISSIONS = arrayOf(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION
    )
    private var locationTrackingEnabled = false

    private var reverse : String? = null

    // 임의로 위치 설정 초기화
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0


    // 사용자 실시간 위치 반영때 사용됨
    //    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val homeMapSearchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                var receivedData = result.data!!.getStringExtra("Address")
                getLocationFromAddress(this, receivedData!!)
                Log.d("itemSet", receivedData.toString())

                // 카메라 이동 설정
                naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
                marker(latitude, longitude)

                // 버튼 설정
                if(binding.homeMapFirstBtnTvLocation.text.toString().isEmpty()){
                    firstLocation = receivedData
                    binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(receivedData)
                    Glide.with(this)
                        .load(R.drawable.home_map_btn_ic_close)
                        .into(binding.homeMapFirstBtnIvLocation)
                }
                else{
                    secondLocation = receivedData
                    binding.homeMapSecondBtnTvLocation.text = extractLocationInfo(receivedData)
                    //firstLocation = binding.homeMapFirstBtnTvLocation.text.toString()
                    Glide.with(this)
                        .load(R.drawable.home_map_btn_ic_close)
                        .into(binding.homeMapSecondBtnIvLocation)
                }
            }
            //예외 처리
            else{

            }
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
            ActivityCompat.requestPermissions(
                this, PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE
            )
        }

    }

    private fun initHomeMapView(){

        clientId = getString(R.string.home_map_naver_api)

        //NAVER 지도 API 호출 및 ID 지정
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(clientId!!)

        //NAVER 객체 얻기 ( 동적 )
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.home_map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.home_map_view, it).commit()
            }

        //인터페이스 객체
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)



        binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(firstLocation.toString())
        if(secondLocation!!.isNotEmpty()){
            binding.homeMapSecondBtnTvLocation.text = extractLocationInfo(secondLocation.toString())
            Glide.with(this)
                .load(R.drawable.home_map_btn_ic_close)
                .into(binding.homeMapSecondBtnIvLocation)
        }

        if(firstLocation!!.isEmpty()){
            homeMapSearchLauncher.launch(
                HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                    firstLocation.toString(), secondLocation.toString()
                )
            )
            Glide.with(this)
                .load(R.drawable.public_ic_add)
                .into(binding.homeMapFirstBtnIvLocation)
        } else{
            Glide.with(this)
                .load(R.drawable.home_map_btn_ic_close)
                .into(binding.homeMapFirstBtnIvLocation)
        }
        binding.homeMapClose.setOnClickListener{
            val intent = Intent().apply{
                putExtra(
                    EXTRA_FIRSTLOCATION,
                    firstLocation
                )
                putExtra(
                    EXTRA_SECONDLOCATION,
                    secondLocation
                )
            }
            setResult(Activity.RESULT_OK, intent)
            finish()

            overridePendingTransition(
                R.anim.home_map_none_fragment,
                R.anim.home_map_slide_down_fragment
            )
        }



        binding.homeMapTvSearch.setOnClickListener{
            if(binding.homeMapSecondBtnTvLocation.text.toString().isEmpty()){
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                        firstLocation.toString(), secondLocation.toString()
                    )
                )
            }
        }

        binding.homeMapFirstBtn.setOnClickListener{
            Log.d("locationFirst", firstLocation!!)
            if(binding.homeMapFirstBtnTvLocation.text.toString().isEmpty()){
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                        firstLocation.toString(), secondLocation.toString()
                    )
                )
            }
            else{
                getLocationFromAddress(this, firstLocation!!)
                naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
                marker(latitude, longitude)
            }
        }

        binding.homeMapFirstBtnIvLocation.setOnClickListener{
            if(binding.homeMapFirstBtnTvLocation.text.toString().isEmpty()){
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                        firstLocation.toString(), secondLocation.toString()
                    )
                )
            }
            else{
                if(binding.homeMapSecondBtnTvLocation.text.toString().isNotEmpty()){
                    binding.homeMapFirstBtnTvLocation.text = binding.homeMapSecondBtnTvLocation.text.toString()
                    secondLocation = ""
                    binding.homeMapSecondBtnTvLocation.text = ""
                    Glide.with(this)
                        .load(R.drawable.public_ic_add)
                        .into(binding.homeMapSecondBtnIvLocation)
                }
                else{
                    firstLocation = ""
                    binding.homeMapFirstBtnTvLocation.text = ""
                    homeMapSearchLauncher.launch(
                        HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                            firstLocation.toString(), secondLocation.toString()
                        )
                    )
                }
            }
        }

        binding.homeMapSecondBtn.setOnClickListener{
//            Log.d("locationSecond", secondLocation!!)
            if(binding.homeMapSecondBtnTvLocation.text.toString().isEmpty()){
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                        firstLocation.toString(), secondLocation.toString()
                    )
                )
            }
            else{
                getLocationFromAddress(this, secondLocation!!)
                naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
                marker(latitude, longitude)

                reverse = firstLocation
                firstLocation = secondLocation
                secondLocation = reverse
                binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(firstLocation.toString())
                binding.homeMapSecondBtnTvLocation.text = extractLocationInfo(secondLocation.toString())
            }
        }

        binding.homeMapSecondBtnIvLocation.setOnClickListener{
            if(binding.homeMapSecondBtnTvLocation.text.toString().isEmpty()){
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                        firstLocation.toString(), secondLocation.toString()
                    )
                )
            }
            else{
                secondLocation = ""
                binding.homeMapSecondBtnTvLocation.text = ""
                Glide.with(this)
                    .load(R.drawable.public_ic_add)
                    .into(binding.homeMapSecondBtnIvLocation)
            }
        }
        if(locationTrackingEnabled){
            binding.trackingLocation.setColorFilter(ContextCompat.getColor(this, R.color.mustard))
        }
        else{
            binding.trackingLocation.setColorFilter(ContextCompat.getColor(this, R.color.white))
        }


    }

    //권한 확인하기
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
            if (!locationSource.isActivated) {
                // 권한 거부됨
                // 위치 추적 거부
                naverMap.locationTrackingMode = LocationTrackingMode.None
                locationTrackingEnabled = false
            //finish()
            }
            else{
                naverMap.locationTrackingMode = LocationTrackingMode.Follow
                locationTrackingEnabled = true
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        naverMap.uiSettings.isScrollGesturesEnabled = false     // 스크롤 제스처 비활성화
        naverMap.uiSettings.isZoomGesturesEnabled = false       // 확대/축소 제스처 비활성화
        naverMap.uiSettings.isTiltGesturesEnabled = false       // 기울이기 제스처 비활성화
        naverMap.uiSettings.isRotateGesturesEnabled = false     // 회전 제스처 비활성화

        if(binding.homeMapFirstBtnTvLocation.text.toString().isNotEmpty()){
            getLocationFromAddress(this, firstLocation!!)
            naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
            marker(latitude, longitude)
        }

        binding.trackingLocation.setOnClickListener{
            if(locationTrackingEnabled){
                binding.trackingLocation.setColorFilter(ContextCompat.getColor(this, R.color.white))
                naverMap.locationTrackingMode = LocationTrackingMode.None
                locationTrackingEnabled = false
            }
            else{
                if(binding.homeMapSecondBtnTvLocation.text.toString().isEmpty()){

                    binding.trackingLocation.setColorFilter(ContextCompat.getColor(this, R.color.mustard))
                    naverMap.locationTrackingMode = LocationTrackingMode.Follow
                    locationTrackingEnabled = true


                    naverMap.addOnLocationChangeListener { location ->
                        latitude = location.latitude
                        longitude = location.longitude
                        Log.d("locationSet", latitude.toString())
                        Log.d("locationSet", longitude.toString())
                        secondLocation = firstLocation
                        binding.homeMapSecondBtnTvLocation.text = extractLocationInfo(secondLocation.toString())
                        Log.d("locationSet", firstLocation.toString())
                        Log.d("locationSet", secondLocation.toString())
                        getAddressFromLocation(this,latitude,longitude)
                        binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(firstLocation.toString())
                        Glide.with(this)
                            .load(R.drawable.home_map_btn_ic_close)
                            .into(binding.homeMapSecondBtnIvLocation)
                        if(binding.homeMapSecondBtnTvLocation.text.toString().isNotEmpty()){
                            naverMap.locationTrackingMode = LocationTrackingMode.None
                        }
                    }

                }
            }
        }

    }
    private fun marker(latitude: Double, longitude: Double) {
        marker.position = LatLng(latitude, longitude)
        marker.map = naverMap

        //getAddressFromLocation(this, latitude, longitude)
    }


    // 좌표 -> 주소 변환
    private fun getAddressFromLocation(context: Context,lat: Double, lng: Double){
        val geocoder = Geocoder(context, Locale.KOREAN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                lat, lng, 1
            ) { addresses ->
                if (addresses.size != 0) {
                    Log.d("Address",(addresses[0].getAddressLine(0)))
                    firstLocation = addresses[0].getAddressLine(0)
                }
            }
        } else {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (addresses != null) {
                Log.d("Address",(addresses[0].getAddressLine(0)))
                firstLocation = addresses[0].getAddressLine(0)
            }
        }
        return
    }

    //주소 -> 좌표 변환
    private fun getLocationFromAddress(context: Context, address: String){
        val geocoder = Geocoder(context, Locale.KOREAN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(
                address, 1
            ) { addresses ->
                if(addresses.isNotEmpty()){
                latitude = addresses[0].latitude
                longitude = addresses[0].longitude
                }
            }
        } else {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (addresses!!.isNotEmpty()) {
                latitude = addresses[0].latitude
                longitude = addresses[0].longitude
            }
        }
        return
    }

}