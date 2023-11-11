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
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
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
import com.wd.woodong2.presentation.home.map.HomeMapSearchActivity.Companion.EXTRA_ADDRESS
import java.io.IOException
import java.util.Locale

class HomeMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val EXTRA_FIRST_LOCATION = "extra_first_location"
        const val EXTRA_SECOND_LOCATION = "extra_second_location"

        var firstLocation : String? ="Unknown Location"
        var secondLocation : String? ="Unknown Location"

        // 임의로 위치 설정 초기화
        var latitude: Double = 0.0
        var longitude: Double = 0.0

        var fullLocationName : String? =""

        fun newIntent(context: Context,firstLoc: String, secondLoc:String)=
            Intent(context, HomeMapActivity::class.java).apply {
                firstLocation = firstLoc
                secondLocation = secondLoc
            }

        fun fullNameLocationInfo(address: String) {
            val parts = address.split(" ")
            fullLocationName = ""

            for ((index, part) in parts.withIndex()) {
                fullLocationName += if (index == 0) {
                    part
                } else {
                    " $part"
                }

                if (part.endsWith("동") || part.endsWith("읍") || part.endsWith("면")) {
                    break
                }
            }
        }

        // 시, 도 추출하기
        fun extractCityInfo(address: String): String{
            val parts = address.split(" ")
            var city: String? = ""
            for ((index, part) in parts.withIndex()) {
                city += if (index == 0) {
                    part
                } else {
                    " $part"
                }
                if (part.endsWith("시") || part.endsWith("도")) {
                    return city.toString()
                }
            }
            return ""
        }

        // 구, 군 까지 추출하기
        fun extractDistrictInfo(address: String):String {
            val parts = address.split(" ")
            var district: String? = ""
            for ((index, part) in parts.withIndex()) {
                district += if (index == 0) {
                    part
                } else {
                    " $part"
                }
                if (part.endsWith("구") || part.endsWith("군")) {
                    return district.toString()
                }
            }
            return ""
        }

        // 구 군 , 동 읍, 면만  추출하기      ->비교할때만 하면 되지 않나??
        fun extractLocationSetInfo(address: String): String {
            val parts = address.split(" ")
            var judge =""
            for ((index, part) in parts.withIndex()){
                judge += if ((part.endsWith("구") || part.endsWith("군"))) {
                    part
                }
                else if(part.endsWith("동") || part.endsWith("읍") || part.endsWith("면")){
                    " $part"
                } else {
                    ""
                }
            }
            return judge
        }

        // 동, 읍, 면만  추출하기
        fun extractLocationInfo(address: String): String {
            val parts = address.split(" ")
            parts.forEach { part ->
                if (part.endsWith("동") || part.endsWith("읍") || part.endsWith("면")) {
                    return part
                }
            }
            return ""
        }

        // 좌표 -> 주소 변환
        fun getAddressFromLocation(context: Context, lat: Double, lng: Double) {
            val geocoder = Geocoder(context, Locale.KOREAN)

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, lng, 2) { addresses ->
                        firstLocation = if (addresses.isNotEmpty()) {
                            val addressLine = addresses[1].getAddressLine(0)
                            Log.d("Address", addressLine)
                            addressLine
                        } else {
                            ""
                        }
                    }
                } else {
                    val addresses = geocoder.getFromLocation(lat, lng, 2)
                    firstLocation = if (!addresses.isNullOrEmpty()) {
                        val addressLine = addresses[1].getAddressLine(0)
                        Log.d("Address", addressLine)
                        addressLine
                    } else {
                        ""
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
        }

        //주소 -> 좌표 변환
        fun getLocationFromAddress(context: Context, address: String) {
            val geocoder = Geocoder(context, Locale.KOREAN)

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocationName(address, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            latitude = addresses[0].latitude
                            longitude = addresses[0].longitude
                        } else {
                            latitude = 35.9078
                            longitude = 127.7669
                        }
                    }
                } else {
                    val addresses = geocoder.getFromLocationName(address, 1)
                    if (addresses?.isNotEmpty() == true) {
                        latitude = addresses[0].latitude
                        longitude = addresses[0].longitude
                    } else {
                        latitude = 35.9078
                        longitude = 127.7669
                    }
                }
            } catch (e: IOException) {

                e.printStackTrace()

            } catch (e: IndexOutOfBoundsException) {

                e.printStackTrace()

            }
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

    private val onBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val intent = Intent().apply{
                putExtra(
                    EXTRA_FIRST_LOCATION,
                    firstLocation
                )
                putExtra(
                    EXTRA_SECOND_LOCATION,
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
    }


    // 사용자 실시간 위치 반영때 사용됨
    //    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val homeMapSearchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK){
                var receivedData = result.data!!.getStringExtra(EXTRA_ADDRESS)
                getLocationFromAddress(this, receivedData!!)

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

        onBackPressedDispatcher.addCallback(this@HomeMapActivity, onBackPressedCallback)

        //상태바 & 아이콘 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 안드로이드 11 이상에서만 동작
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 안드로이드 6.0 이상에서만 동작
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } // 안드로이드 6.0 이하는 상태바 아이콘 색상 변경 지원 안함


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
                    EXTRA_FIRST_LOCATION,
                    firstLocation
                )
                putExtra(
                    EXTRA_SECOND_LOCATION,
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
            else{
                Toast.makeText(this, "위치 설정은 최대 두개까지 입니다.", Toast.LENGTH_SHORT).show()
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
                Log.d("location",firstLocation.toString())
                Log.d("location",secondLocation.toString())
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

        naverMap.uiSettings.isZoomControlEnabled = false
        // 지도의 초기 확대 레벨 설정
//        val initialZoomLevel = 14.0 // 예시로 14.0으로 설정
//
//        // Naver 지도의 확대 레벨 조절
//        naverMap.minZoom = initialZoomLevel // 최소 확대 레벨 설정
//        naverMap.maxZoom = initialZoomLevel // 최대 확대 레벨 설정
//        naverMap.moveCamera(CameraUpdate.zoomTo(initialZoomLevel)) // 초기 확대 레벨 설정

        //초기 위치 설정
        getLocationFromAddress(this, firstLocation!!)
        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
        marker(latitude, longitude)

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
                        naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
                        marker(latitude, longitude)
                        naverMap.locationTrackingMode = LocationTrackingMode.None
                        binding.trackingLocation.setColorFilter(ContextCompat.getColor(this, R.color.white))
                        locationTrackingEnabled = false

                        reverse = firstLocation
                        getAddressFromLocation(this,latitude,longitude)

                        if(extractLocationSetInfo(reverse.toString()) !=
                            extractLocationSetInfo(firstLocation.toString())){
                            Log.d("location",extractLocationSetInfo(reverse.toString()))
                            Log.d("location",extractLocationSetInfo(firstLocation.toString()))
                            secondLocation = reverse
                            binding.homeMapSecondBtnTvLocation.text = extractLocationInfo(secondLocation.toString())
                            binding.homeMapClSecondBtn.setBackgroundResource(R.drawable.home_map_btn_uncheck)
                            binding.homeMapSecondBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.curry_yellow_txt))
                            binding.homeMapSecondBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.curry_yellow_txt))
                            Glide.with(this)
                                .load(R.drawable.home_map_btn_ic_close)
                                .override(96, 96)
                                .centerCrop()
                                .into(binding.homeMapSecondBtnIvLocation)

                            binding.homeMapFirstBtnTvLocation.text = extractLocationInfo(firstLocation.toString())
                            getAddressFromLocation(this,latitude,longitude)

                            binding.homeMapClFirstBtn.setBackgroundResource(R.drawable.home_map_btn_check)
                            binding.homeMapFirstBtnTvLocation.setTextColor(ContextCompat.getColor(this, R.color.normal_gray_txt))
                            binding.homeMapFirstBtnIvLocation.setColorFilter(ContextCompat.getColor(this, R.color.normal_gray_txt))
                            Glide.with(this)
                                .load(R.drawable.home_map_btn_ic_close)
                                .override(96, 96)
                                .centerCrop()
                                .into(binding.homeMapFirstBtnIvLocation)
                        }
                    }
                } else{

                }
            }else{
                Toast.makeText(this, "위치 설정은 최대 두개까지 입니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun marker(latitude: Double, longitude: Double) {
        marker.position = LatLng(latitude, longitude)
        marker.map = naverMap

        //getAddressFromLocation(this, latitude, longitude)
    }




}