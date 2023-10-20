package com.wd.woodong2.presentation.home.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.wd.woodong2.databinding.HomeDetailActivityBinding
import com.wd.woodong2.presentation.home.content.HomeItem

class HomeDetailActivity : AppCompatActivity() {
    private lateinit var binding: HomeDetailActivityBinding

    companion object {
        private const val EXTRA_HOME_ITEM = "extra_home_item"
        fun homeDetailActivityNewIntent(context: Context, homeItem: HomeItem): Intent =
            Intent(context, HomeDetailActivity::class.java).apply {
                putExtra(EXTRA_HOME_ITEM, homeItem)
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeItem: HomeItem? = intent.getParcelableExtra(EXTRA_HOME_ITEM, HomeItem::class.java)
        homeItem?.let {
            displayData(it)
        } ?: run {
            Toast.makeText(this, "데이터를 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }



    private fun displayData(homeItem: HomeItem) {
        binding.txtHomeTitle.text = homeItem.title
        binding.txtHomeDescription.text = homeItem.description
        binding.txtHomeTag.text = homeItem.tag
        binding.imgHomeThumnail.load(homeItem.thumbnail) {
            crossfade(true)
        }
    }
}
