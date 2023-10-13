package com.wd.woodong2.presentation.chat.content

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatViewModel : ViewModel(
//    private val chat: GetSearchImageUseCase,
) {
    private val _chatList = MutableLiveData<MutableList<ChatItem>>()
    val chatList: LiveData<MutableList<ChatItem>>
        get() = _chatList

    init {
        val testChatItems = mutableListOf<ChatItem>()
        for (i in 1..10) {
            val chatItem = ChatItem.GroupChatItem(
                title = "Chat Title $i",
                thumbnail = "Thumbnail URL $i",
                location = "Location $i",
                contents = "Chat Contents $i",
                timeStamp = "Time Stamp $i"
            )
            testChatItems.add(chatItem)
        }
        _chatList.value = testChatItems
    }

    fun onClickSearchItem(item: ChatItem) {
//        val appContext = MyApplication.instance.applicationContext
//        val intent = Intent(context, ChatDetailActivity::class.java)
//        intent.putExtra("chatItem", item)
//        context.startActivity(intent)

        Log.d("test", "아이템 클릭 됨")
    }
}


class ChatViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel() as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}