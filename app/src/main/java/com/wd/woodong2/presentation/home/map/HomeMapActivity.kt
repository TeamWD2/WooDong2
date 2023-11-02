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
import com.wd.woodong2.presentation.home.map.HomeMapSearchActivity.Companion.EXTRA_ADDRESS
import java.util.Locale

class HomeMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val EXTRA_FIRSTLOCATION = "extra_firstlocation"
        const val EXTRA_SECONDLOCATION = "extra_secondlocation"

        private var firstLocation : String? ="Unknown Location"
        private var secondLocation : String? ="Unknown Location"

        // 임의로 위치 설정 초기화
        var latitude: Double = 0.0
        var longitude: Double = 0.0
        fun newIntent(context: Context,firstLoc: String, secondLoc:String)=
            Intent(context, HomeMapActivity::class.java).apply {
                firstLocation = firstLoc
                secondLocation = secondLoc
            }
        // 동, 읍, 면 추출하기
        fun extractLocationInfo(address: String): String {
            val parts = address.split(" ")
            for (part in parts) {
                if (part.endsWith("동") || part.endsWith("읍") || part.endsWith("면")) {
                    return part
                }
            }
            return ""
        }
        // 좌표 -> 주소 변환
        private fun getAddressFromLocation(context: Context,lat: Double, lng: Double){
            val geocoder = Geocoder(context, Locale.KOREAN)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    lat, lng, 2
                ) { addresses ->
                    if (addresses.size != 0) {
                        Log.d("Address",(addresses[1].getAddressLine(0)))
                        firstLocation = addresses[1].getAddressLine(0)
                    }
                }
            } else {
                val addresses = geocoder.getFromLocation(
                    lat, lng, 2
                )
                if (addresses != null) {
                    Log.d("Address",(addresses[1].getAddressLine(0)))
                    firstLocation = addresses[1].getAddressLine(0)
                }
            }
            return
        }

        //주소 -> 좌표 변환
        fun getLocationFromAddress(context: Context, address: String){
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




    // 사용자 실시간 위치 반영때 사용됨
    //    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val homeMapSearchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                var receivedData = result.data!!.getStringExtra(EXTRA_ADDRESS)
                getLocationFromAddress(this, receivedData!!)
                Log.d("itemSet", receivedData.toString())

                // 카메라 이동 설정
                naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
                marker(latitude, longitude)

                // 1위치 없을때
                if(binding.homeMapFirstBtnTvLocation.text.toString().isEmpty()){
                    firstLocation = receivedData
                    binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(receivedData)
                    binding.homeMapClFirstBtn.setBackgroundResource(R.drawable.home_map_btn_check)
                    binding.homeMapFirstBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.normal_gray_txt))
                    binding.homeMapFirstBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.normal_gray_txt))
                    binding.homeMapClSecondBtn.setBackgroundResource(R.drawable.home_map_btn_empty)
                    binding.homeMapSecondBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.black))
                    Glide.with(this)
                        .load(R.drawable.home_map_btn_ic_close)
                        .override(96, 96)
                        .centerCrop()
                        .into(binding.homeMapFirstBtnIvLocation)
                }
                else{//1위치 있을때
                    reverse = firstLocation
                    firstLocation = receivedData
                    secondLocation = reverse
                    binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(receivedData)
                    binding.homeMapClFirstBtn.setBackgroundResource(R.drawable.home_map_btn_check)
                    binding.homeMapFirstBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.normal_gray_txt))
                    binding.homeMapFirstBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.normal_gray_txt))
                    Glide.with(this)
                        .load(R.drawable.home_map_btn_ic_close)
                        .override(96, 96)
                        .centerCrop()
                        .into(binding.homeMapFirstBtnIvLocation)
                    binding.homeMapSecondBtnTvLocation.text = extractLocationInfo(secondLocation.toString())
                    binding.homeMapClSecondBtn.setBackgroundResource(R.drawable.home_map_btn_uncheck)
                    binding.homeMapSecondBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.curry_yellow_txt))
                    binding.homeMapSecondBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.curry_yellow_txt))
                    Glide.with(this)
                        .load(R.drawable.home_map_btn_ic_close)
                        .override(96, 96)
                        .centerCrop()
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

        clientId = getString(R.string.home_map_naver_api)

        //NAVER 지도 API 호출 및 ID 지정
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(clientId!!)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
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

        //NAVER 객체 얻기 ( 동적 )
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.home_map_view) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.home_map_view, it).commit()
            }

        //인터페이스 객체
        mapFragment.getMapAsync(this)

        //처음 map에 들어 갈때 1위치 출력
        //binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(firstLocation.toString())

        //1위치 없을때
        if(firstLocation!!.isEmpty()){
            binding.homeMapClFirstBtn.setBackgroundResource(R.drawable.home_map_btn_empty)
            binding.homeMapFirstBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.black))
            homeMapSearchLauncher.launch(
                HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                    firstLocation.toString(), secondLocation.toString()
                )
            )
            Glide.with(this)
                .load(R.drawable.public_ic_add)
                .override(96, 96)
                .centerCrop()
                .into(binding.homeMapFirstBtnIvLocation)
        } else{//1위치 있을때 -> 1위치 출력
            binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(firstLocation.toString())
            binding.homeMapClFirstBtn.setBackgroundResource(R.drawable.home_map_btn_check)
            binding.homeMapFirstBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.normal_gray_txt))
            binding.homeMapFirstBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.normal_gray_txt))
            Glide.with(this)
                .load(R.drawable.home_map_btn_ic_close)
                .override(96, 96)
                .centerCrop()
                .into(binding.homeMapFirstBtnIvLocation)
        }

        //2위치 있을때 -> 2위치 출력
        if(secondLocation!!.isNotEmpty()){
            binding.homeMapSecondBtnTvLocation.text = extractLocationInfo(secondLocation.toString())
            binding.homeMapClSecondBtn.setBackgroundResource(R.drawable.home_map_btn_uncheck)
            binding.homeMapSecondBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.curry_yellow_txt))
            binding.homeMapSecondBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.curry_yellow_txt))
            Glide.with(this)
                .load(R.drawable.home_map_btn_ic_close)
                .override(96, 96)
                .centerCrop()
                .into(binding.homeMapSecondBtnIvLocation)
        }else{//2위치 없을때
            binding.homeMapClSecondBtn.setBackgroundResource(R.drawable.home_map_btn_empty)
            binding.homeMapSecondBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.black))
        }



        //종료
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


        //search 클릭시 2위치 없을때
        binding.homeMapTvSearch.setOnClickListener{
            if(binding.homeMapSecondBtnTvLocation.text.toString().isEmpty()){
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                        firstLocation.toString(), secondLocation.toString()
                    )
                )
            }
        }

        //1위치 버튼 클릭시
        binding.homeMapFirstBtn.setOnClickListener{
            if(binding.homeMapFirstBtnTvLocation.text.toString().isEmpty()){
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                        firstLocation.toString(), secondLocation.toString()
                    )
                )
            }
            else{
                binding.homeMapClFirstBtn.setBackgroundResource(R.drawable.home_map_btn_check)
                binding.homeMapFirstBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.normal_gray_txt))
                binding.homeMapFirstBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.normal_gray_txt))
                getLocationFromAddress(this, firstLocation!!)
                naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
                marker(latitude, longitude)
            }
        }
        //첫번재 이미지 클릭시
        binding.homeMapFirstBtnIvLocation.setOnClickListener{
            if(binding.homeMapFirstBtnTvLocation.text.toString().isEmpty()){//1위치 없을때
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                        firstLocation.toString(), secondLocation.toString()
                    )
                )
            }
            else{//1위치 있을때
                if(binding.homeMapSecondBtnTvLocation.text.toString().isNotEmpty()){//2위치 있을때
                    binding.homeMapFirstBtnTvLocation.text = binding.homeMapSecondBtnTvLocation.text.toString()
                    binding.homeMapClSecondBtn.setBackgroundResource(R.drawable.home_map_btn_empty)
                    binding.homeMapSecondBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.black))
                    secondLocation = ""
                    binding.homeMapSecondBtnTvLocation.text = ""
                    binding.homeMapClFirstBtn.setBackgroundResource(R.drawable.home_map_btn_check)
                    binding.homeMapFirstBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.normal_gray_txt))
                    binding.homeMapFirstBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.normal_gray_txt))
                    binding.homeMapClSecondBtn.setBackgroundResource(R.drawable.home_map_btn_uncheck)
                    binding.homeMapSecondBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.curry_yellow_txt))
                    binding.homeMapSecondBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.curry_yellow_txt))
                    Glide.with(this)
                        .load(R.drawable.public_ic_add)
                        .override(96, 96)
                        .centerCrop()
                        .into(binding.homeMapSecondBtnIvLocation)
                }
                else{//2위치 없을때
                    binding.homeMapClFirstBtn.setBackgroundResource(R.drawable.home_map_btn_empty)
                    binding.homeMapFirstBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.black))
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
        //2위치 버튼 클릭시
        binding.homeMapSecondBtn.setOnClickListener{
            if(binding.homeMapSecondBtnTvLocation.text.toString().isEmpty()){//2위치 없을때
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                        firstLocation.toString(), secondLocation.toString()
                    )
                )
            }
            else{//2위치 있을때
                getLocationFromAddress(this, secondLocation!!)
                naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
                marker(latitude, longitude)
                reverse = firstLocation
                firstLocation = secondLocation
                secondLocation = reverse
                binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(firstLocation.toString())
                binding.homeMapSecondBtnTvLocation.text = extractLocationInfo(secondLocation.toString())
                binding.homeMapClFirstBtn.setBackgroundResource(R.drawable.home_map_btn_check)
                binding.homeMapFirstBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.normal_gray_txt))
                binding.homeMapFirstBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.normal_gray_txt))
                binding.homeMapClSecondBtn.setBackgroundResource(R.drawable.home_map_btn_uncheck)
                binding.homeMapSecondBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.curry_yellow_txt))
                binding.homeMapSecondBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.curry_yellow_txt))

            }
        }
        //2위치 이미지 클릭시
        binding.homeMapSecondBtnIvLocation.setOnClickListener{
            if(binding.homeMapSecondBtnTvLocation.text.toString().isEmpty()){//2위치 없을때
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity,
                        firstLocation.toString(), secondLocation.toString()
                    )
                )
            }
            else{//2위치 있을때
                binding.homeMapClSecondBtn.setBackgroundResource(R.drawable.home_map_btn_empty)
                binding.homeMapSecondBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.black))
                secondLocation = ""
                binding.homeMapSecondBtnTvLocation.text = ""
                Glide.with(this)
                    .load(R.drawable.public_ic_add)
                    .override(96, 96)
                    .centerCrop()
                    .into(binding.homeMapSecondBtnIvLocation)
            }
        }
        if(locationTrackingEnabled){
            binding.trackingLocation.setColorFilter(ContextCompat.getColor(this, R.color.red))
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
                initHomeMapView()
                //naverMap.locationTrackingMode = LocationTrackingMode.Follow
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
        binding.trackingLocation.setOnClickListener{
            locationTrackingEnabled = true
            if(firstLocation == "" &&   //1위치 2위치 없을때
                secondLocation == "" &&
                binding.homeMapFirstBtnTvLocation.text.toString().isEmpty() &&
                binding.homeMapSecondBtnTvLocation.text.toString().isEmpty()){
                if(locationTrackingEnabled){
                    naverMap.locationTrackingMode = LocationTrackingMode.Follow
                    binding.trackingLocation.setColorFilter(ContextCompat.getColor(this, R.color.red))
                    naverMap.addOnLocationChangeListener{location ->
                        latitude = location.latitude
                        longitude = location.longitude
                        Log.d("tracking", location.latitude.toString())
                        Log.d("tracking", location.longitude.toString())
                        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
                        marker(latitude, longitude)
                        naverMap.locationTrackingMode = LocationTrackingMode.None
                        binding.trackingLocation.setColorFilter(ContextCompat.getColor(this, R.color.white))
                        locationTrackingEnabled = false
                        //도로명 주소
                        //1위치 설정
                        getAddressFromLocation(this,latitude,longitude)
                        binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(firstLocation.toString())
                        binding.homeMapClFirstBtn.setBackgroundResource(R.drawable.home_map_btn_check)
                        binding.homeMapFirstBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.normal_gray_txt))
                        binding.homeMapFirstBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.normal_gray_txt))
                        Glide.with(this)
                            .load(R.drawable.home_map_btn_ic_close)
                            .override(96, 96)
                            .centerCrop()
                            .into(binding.homeMapFirstBtnIvLocation)
                    }
                }else{

                }
            }
            else if(firstLocation != "" &&  //1위치만 있을때
                secondLocation == "" &&
                binding.homeMapFirstBtnTvLocation.text.toString().isNotEmpty() &&
                binding.homeMapSecondBtnTvLocation.text.toString().isEmpty()){
                if(locationTrackingEnabled){
                    naverMap.locationTrackingMode = LocationTrackingMode.Follow
                    binding.trackingLocation.setColorFilter(ContextCompat.getColor(this, R.color.red))
                    naverMap.addOnLocationChangeListener{location ->
                        latitude = location.latitude
                        longitude = location.longitude
                        Log.d("tracking", location.latitude.toString())
                        Log.d("tracking", location.longitude.toString())
                        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
                        marker(latitude, longitude)
                        naverMap.locationTrackingMode = LocationTrackingMode.None
                        binding.trackingLocation.setColorFilter(ContextCompat.getColor(this, R.color.white))
                        locationTrackingEnabled = false

                        secondLocation = firstLocation
                        binding.homeMapSecondBtnTvLocation.text = extractLocationInfo(secondLocation.toString())
                        binding.homeMapClSecondBtn.setBackgroundResource(R.drawable.home_map_btn_uncheck)
                        binding.homeMapSecondBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.curry_yellow_txt))
                        binding.homeMapSecondBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.curry_yellow_txt))
                        Glide.with(this)
                            .load(R.drawable.home_map_btn_ic_close)
                            .override(96, 96)
                            .centerCrop()
                            .into(binding.homeMapSecondBtnIvLocation)
                        //도로명 주소
                        getAddressFromLocation(this,latitude,longitude)
                        binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(firstLocation.toString())
                        binding.homeMapClFirstBtn.setBackgroundResource(R.drawable.home_map_btn_check)
                        binding.homeMapFirstBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.normal_gray_txt))
                        binding.homeMapFirstBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.normal_gray_txt))
                        Glide.with(this)
                            .load(R.drawable.home_map_btn_ic_close)
                            .override(96, 96)
                            .centerCrop()
                            .into(binding.homeMapFirstBtnIvLocation)
                    }
                } else{

                }
            }else{

            }
        }

    }
    private fun marker(latitude: Double, longitude: Double) {
        marker.position = LatLng(latitude, longitude)
        marker.map = naverMap

        //getAddressFromLocation(this, latitude, longitude)
    }




}