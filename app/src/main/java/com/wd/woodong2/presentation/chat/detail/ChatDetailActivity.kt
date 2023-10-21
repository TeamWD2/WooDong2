package com.wd.woodong2.presentation.chat.detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.wd.woodong2.databinding.ChatDetailActivityBinding
import com.wd.woodong2.presentation.chat.content.ChatItem

class ChatDetailActivity : AppCompatActivity() {
    companion object {
        const val CHAT_ITEM = "chat_item"
        const val USER_ID = "user_id"

        fun newIntentForDetail(context: Context, item: ChatItem, userId: String): Intent =
            Intent(context, ChatDetailActivity::class.java).apply {
                putExtra(CHAT_ITEM, item)
                putExtra(USER_ID, userId)
            }
    }

    // Test
    var receiveItem: ChatItem? = null
    var receivedUserId = ""
    var chatKey: String = ""


    private var _binding: ChatDetailActivityBinding? = null
    private val binding get() = _binding!!

    private val chatDetailViewModel: ChatDetailViewModel by viewModels {
        // Test 후에 null 처리
        ChatDetailViewModelFactory(receiveItem!!, receivedUserId)
    }

    private val chatDetailItemListAdapter by lazy {
        ChatDetailListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ChatDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initInfo()
        initView()
        initModel()
    }

    private fun initInfo() {
        val receivedChatItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(CHAT_ITEM, ChatItem::class.java)
        } else {
            intent.getParcelableExtra(CHAT_ITEM)
        }

        // Test
        receiveItem = receivedChatItem
        receivedUserId = intent.getStringExtra(USER_ID) ?: ""

        if (receivedChatItem != null) {
            chatKey = receivedChatItem.id ?: ""
        }
    }

    private fun initView() = with(binding) {

        when (receiveItem) {
            is ChatItem.GroupChatItem -> {
                txtChatType.text = (receiveItem as ChatItem.GroupChatItem).title
                txtMemberNum.text = "## / ##명"
            }

            is ChatItem.PrivateChatItem -> {
                txtChatType.text = (receiveItem as ChatItem.PrivateChatItem).title
                txtMemberNum.text = "## / ##명"
            }

            null -> {
                //TODO
            }
        }

        recyclerViewChat.apply {
            adapter = chatDetailItemListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        btnSend.setOnClickListener {
            chatDetailViewModel.sendMessage(
                edtAddChat.text.toString()
            )
            edtAddChat.text.clear()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initModel() {
        chatDetailViewModel.messageList.observe(this) { itemList ->
            chatDetailItemListAdapter.submitList(itemList.toMutableList())
        }
        chatDetailViewModel.isLoading.observe(this) { loadingState ->
            binding.progressBar.isVisible = loadingState
        }
    }
}