package com.wd.woodong2.presentation.chat.detail

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wd.woodong2.R
import com.wd.woodong2.databinding.ChatDetailActivityBinding
import com.wd.woodong2.presentation.chat.content.ChatItem

class ChatDetailActivity : AppCompatActivity() {
    companion object {
        const val CHAT_ITEM = "chat_item"

        fun newIntentForDetail(context: Context, item: ChatItem): Intent =
            Intent(context, ChatDetailActivity::class.java).apply {
                putExtra(CHAT_ITEM, item)
            }
    }

    // Test
    var receiveItem: ChatItem? = null
    var chatKey: String = ""


    private var _binding: ChatDetailActivityBinding? = null
    private val binding get() = _binding!!

    private val chatDetailViewModel: ChatDetailViewModel by viewModels {
        // Test 후에 null 처리
        ChatDetailViewModelFactory(receiveItem!!)
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

        if (receivedChatItem != null) {
            chatKey = receivedChatItem.id ?: ""
        }
    }

    private fun initView() = with(binding) {
        //상태바 & 아이콘 색상 변경
        window.statusBarColor = ContextCompat.getColor(this@ChatDetailActivity, R.color.egg_yellow_toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // 안드로이드 11 이상에서만 동작
            window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 안드로이드 6.0 이상에서만 동작
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } // 안드로이드 6.0 이하는 상태바 아이콘 색상 변경 지원 안함

        root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            root.getWindowVisibleDisplayFrame(r)
            val screenHeight = root.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) {
                recyclerViewChat.scrollToPosition(chatDetailItemListAdapter.itemCount - 1)
            }
        }

        when (val item = receiveItem) {
            is ChatItem.GroupChatItem -> {
                txtChatType.text = item.title
                txtMemberNum.text = "## / ${item.memberLimit}명"
            }

            is ChatItem.PrivateChatItem -> {
                txtChatType.text = item.title
                txtMemberNum.visibility = View.GONE
            }

            null -> {
                //TODO
            }
        }

        recyclerViewChat.apply {
            adapter = chatDetailItemListAdapter
            layoutManager = LinearLayoutManager(context)

            var isInitialLoad = true

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!isInitialLoad && !recyclerViewChat.canScrollVertically(-1)) {
                        chatDetailViewModel.getMessageItem()
                    }
                    isInitialLoad = false
                }
            })
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

    private fun initModel() = with(binding) {
        chatDetailViewModel.messageList.observe(this@ChatDetailActivity) { itemList ->
            chatDetailItemListAdapter.submitList(itemList.toMutableList())

            val isAtBottom = !recyclerViewChat.canScrollVertically(1)
            chatDetailItemListAdapter.submitList(itemList.toMutableList())
            recyclerViewChat.post {
                if (isAtBottom) {
                    recyclerViewChat.scrollToPosition(itemList.size - 1)
                }
            }
        }
        chatDetailViewModel.isLoading.observe(this@ChatDetailActivity) { loadingState ->
            binding.progressBar.isVisible = loadingState
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatDetailViewModel.destroyAll()
    }

}