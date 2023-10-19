package com.wd.woodong2.presentation.home.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.load
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

    private var firstLocation : String? ="Unknown Location"
    private var secondLocation : String? ="Unknown Location"

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
                    binding.homeMapFirstBtnIvLocation.load(R.drawable.home_map_btn_ic_close){ size(24, 24) }
                    (binding.homeMapFirstBtnIvLocation.layoutParams as ConstraintLayout.LayoutParams).horizontalBias = 0.99f
                }
                else{
                    secondLocation = receivedData
                    binding.homeMapSecondBtnTvLocation.text = receivedData
                    binding.homeMapSecondBtnIvLocation.load(R.drawable.home_map_btn_ic_close){ size(24, 24) }
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

        binding.homeMapClose.setOnClickListener{
            finish()
            overridePendingTransition(
                R.anim.home_map_none_fragment,
                R.anim.home_map_slide_down_fragment
            )
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
                    binding.homeMapSecondBtnIvLocation.load(R.drawable.public_ic_add) { size(24, 24) }
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
                binding.homeMapSecondBtnIvLocation.load(R.drawable.public_ic_add){ size(24, 24) }
                (binding.homeMapSecondBtnIvLocation.layoutParams as ConstraintLayout.LayoutParams).horizontalBias = 0f
            }
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
                latitude = addresses[0].latitude
                longitude = addresses[0].longitude
                return LatLng(latitude, longitude)
            } else {
                return null
            }
        }.getOrElse {
            it.printStackTrace()
            null
        }
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