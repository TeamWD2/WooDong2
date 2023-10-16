package com.wd.woodong2.presentation.chat.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.databinding.ChatDetailActivityBinding

class ChatDetailActivity : AppCompatActivity() {
    private lateinit var binding: ChatDetailActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {

    }
}