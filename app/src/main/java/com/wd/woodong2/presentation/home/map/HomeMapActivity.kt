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
        const val EXTRA_FIRSTLOCATION = "extra_firstlocation"
        const val EXTRA_SECONDLOCATION = "extra_secondlocation"

        private var firstLocation : String? ="Unknown Location"
        private var secondLocation : String? ="Unknown Location"
        fun newIntent(context: Context,firstLoc: String, secondLoc:String)=//, homeItem: HomeItem) =
            Intent(context, HomeMapActivity::class.java).apply {
                //HomeMapItem = homeItem
                firstLocation = firstLoc
                secondLocation = secondLoc
            }

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private var clientId : String? = null
    private lateinit var binding : HomeMapActivityBinding
    private lateinit var naverMap: NaverMap
    private val marker = Marker()
    private lateinit var locationSource: FusedLocationSource
    private val PERMISSIONS = arrayOf(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION
    )


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

                receivedData = extractLocationInfo(receivedData)
                Log.d("itemSet", receivedData.toString())

                // 버튼 설정
                if(binding.homeMapFirstBtnTvLocation.text.toString().isEmpty()){
                    firstLocation = receivedData
                    binding.homeMapFirstBtnTvLocation.text = receivedData
                    secondLocation = binding.homeMapSecondBtnTvLocation.text.toString()

                    //binding.homeMapFirstBtnIvLocation.load(R.drawable.home_map_btn_ic_close){ size(24, 24) }
                    Glide.with(this)
                        .load(R.drawable.home_map_btn_ic_close)
                        .into(binding.homeMapFirstBtnIvLocation)
                    (binding.homeMapFirstBtnIvLocation.layoutParams as ConstraintLayout.LayoutParams).horizontalBias = 0.99f
                }
                else{
                    secondLocation = receivedData
                    binding.homeMapSecondBtnTvLocation.text = receivedData
                    firstLocation = binding.homeMapFirstBtnTvLocation.text.toString()
                    //binding.homeMapSecondBtnIvLocation.load(R.drawable.home_map_btn_ic_close){ size(24, 24) }
                    Glide.with(this)
                        .load(R.drawable.home_map_btn_ic_close)
                        .into(binding.homeMapSecondBtnIvLocation)
                    (binding.homeMapSecondBtnIvLocation.layoutParams as ConstraintLayout.LayoutParams).horizontalBias = 0.99f
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
            ActivityCompat.requestPermissions(this, PERMISSIONS,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

    }

    private fun initHomeMapView(){

        binding.homeMapFirstBtnTvLocation.text = firstLocation
        if(secondLocation!!.isNotEmpty()){
            binding.homeMapSecondBtnTvLocation.text = secondLocation
            Glide.with(this)
                .load(R.drawable.home_map_btn_ic_close)
                .into(binding.homeMapSecondBtnIvLocation)
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
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity)
                )
            }
        }

        binding.homeMapFirstBtn.setOnClickListener{
            Log.d("locationFirst", firstLocation!!)
            if(binding.homeMapFirstBtnTvLocation.text.toString().isEmpty()){
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity)
                )
            }
            else{
                firstLocation = binding.homeMapFirstBtnTvLocation.text.toString()
                Log.d("locationFirst", firstLocation!!)
                getLocationFromAddress(this, firstLocation!!)
                naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
                marker(latitude, longitude)
            }
        }

        binding.homeMapFirstBtnIvLocation.setOnClickListener{
            if(binding.homeMapFirstBtnTvLocation.text.toString().isEmpty()){
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity)
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
                    (binding.homeMapSecondBtnIvLocation.layoutParams as ConstraintLayout.LayoutParams).horizontalBias = 0f

                }
            }
        }

        binding.homeMapSecondBtn.setOnClickListener{
            Log.d("locationSecond", secondLocation!!)
            if(binding.homeMapSecondBtnTvLocation.text.toString().isEmpty()){
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity)
                )
            }
            else{
                secondLocation = binding.homeMapSecondBtnTvLocation.text.toString()
                getLocationFromAddress(this, secondLocation!!)
                naverMap.moveCamera(CameraUpdate.scrollTo(LatLng(latitude, longitude)))
                marker(latitude, longitude)

                reverse = firstLocation
                firstLocation = secondLocation
                secondLocation = reverse
                binding.homeMapFirstBtnTvLocation.text = firstLocation
                binding.homeMapSecondBtnTvLocation.text = secondLocation
            }
        }

        binding.homeMapSecondBtnIvLocation.setOnClickListener{
            if(binding.homeMapSecondBtnTvLocation.text.toString().isEmpty()){
                homeMapSearchLauncher.launch(
                    HomeMapSearchActivity.newIntent(this@HomeMapActivity)
                )
            }
            else{
                secondLocation = ""
                binding.homeMapSecondBtnTvLocation.text = ""
                Glide.with(this)
                    .load(R.drawable.public_ic_add)
                    .into(binding.homeMapSecondBtnIvLocation)
                (binding.homeMapSecondBtnIvLocation.layoutParams as ConstraintLayout.LayoutParams).horizontalBias = 0f
            }
        }
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
    }
    private fun marker(latitude: Double, longitude: Double) {
        marker.position = LatLng(latitude, longitude)
        marker.map = naverMap

        getAddressFromLocation(this, latitude, longitude)
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
                }
            }
        } else {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (addresses != null) {
                Log.d("Address",(addresses[0].getAddressLine(0)))
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
    // 동, 읍, 면 추출하기
    private fun extractLocationInfo(address: String): String {
        val parts = address.split(" ")
        for (part in parts) {
            if (part.contains("동") || part.contains("읍") || part.contains("면")) {
                return part
            }
        }
        return ""
    }
}