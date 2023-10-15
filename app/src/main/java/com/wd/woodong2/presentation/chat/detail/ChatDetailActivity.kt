package com.wd.woodong2.presentation.chat.detail

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wd.woodong2.databinding.ChatDetailActivityBinding
import com.wd.woodong2.presentation.chat.content.ChatItem

class ChatDetailActivity : AppCompatActivity() {
    companion object {
        const val CHAT_ITEM = "chat_item"
    }

    private lateinit var binding: ChatDetailActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() = with(binding) {
        val receivedChatItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CHAT_ITEM, ChatItem::class.java)
        } else {
            intent.getParcelableExtra(CHAT_ITEM)
        }
        if (receivedChatItem != null) {

        }
    }
}