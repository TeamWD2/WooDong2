package com.wd.woodong2.presentation.home.map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.wd.woodong2.R
import com.wd.woodong2.databinding.HomeMapSearchActivityBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeMapSearchActivity : AppCompatActivity() {

    companion object {

        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 2944f118955b6cf31d773eea99bcc7d2"  // REST API 키

        //private lateinit var HomeMapItem: HomeItem
        fun newIntent(context: Context)=//, homeItem: HomeItem) =
        Intent(context, HomeMapSearchActivity::class.java).apply {
                //HomeMapItem = homeItem
            }

    }

    private lateinit var binding : HomeMapSearchActivityBinding
    private val listItems = arrayListOf<HomeMapSearchItem>()
    private val listAdapter : HomeMapSearchListAdapter by lazy{
        HomeMapSearchListAdapter(
            onClickItem = { _, item ->
                Log.d("itemSet", item.toString())
            val intent = Intent().apply{
                putExtra(
                    "Address",
                    item.address
                )
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        },
            itemList = listItems
        )
    }
    private var address = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomeMapSearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(
            R.anim.home_map_search_right,
            R.anim.home_map_none_fragment
        )

        initView()
    }
    private fun initView(){
        binding.homeMapSearchRc.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.homeMapSearchRc.adapter = listAdapter

        binding.homeMapSearchClose.setOnClickListener{
            finish()
            overridePendingTransition(
                R.anim.home_map_none_fragment,
                R.anim.home_map_search_left
            )
        }
        binding.etSearch.setOnClickListener {
            hideKeyboard()
        }
        binding.homeMapSearchBtn.setOnClickListener{
            address = binding.etSearch.text.toString()
            addressSearch(address)
        }
    }
    private fun hideKeyboard() {
        val view = this.currentFocus
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
    //검색 함수
    private fun addressSearch(keyword: String) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getAddressSearch(API_KEY, keyword)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<AddressSearchResponse> {
            override fun onResponse(
                call: Call<AddressSearchResponse>,
                response: Response<AddressSearchResponse>
            ) {
                addItems(response.body())
                Log.d("Test", "Raw: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<AddressSearchResponse>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }
    // 검색 결과 처리 함수
    private fun addItems(searchResult: AddressSearchResponse?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = HomeMapSearchItem(
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)

                listAdapter.notifyDataSetChanged()
            }
        }
        else{
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}