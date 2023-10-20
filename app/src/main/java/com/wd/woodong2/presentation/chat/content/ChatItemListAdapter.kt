package com.wd.woodong2.presentation.chat.content

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wd.woodong2.databinding.ChatListItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChatItemListAdapter(
    private val onClick: (ChatItem) -> Unit
) : ListAdapter<ChatItem, ChatItemListAdapter.ViewHolder>(

    object : DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(
            oldItem: ChatItem,
            newItem: ChatItem
        ): Boolean = if (oldItem is ChatItem.GroupChatItem && newItem is ChatItem.GroupChatItem) {
            oldItem.id == newItem.id
        } else {
            oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ChatItem,
            newItem: ChatItem
        ): Boolean = oldItem == newItem
    }
) {

    enum class ChatItemViewType {
        GROUP, PRIVATE
    }

    abstract class ViewHolder(
        root: View
    ) : RecyclerView.ViewHolder(root) {
        abstract fun onBind(item: ChatItem)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ChatItem.GroupChatItem -> ChatItemViewType.GROUP.ordinal
        is ChatItem.PrivateChatItem -> ChatItemViewType.PRIVATE.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            ChatItemViewType.GROUP.ordinal ->
                GroupChatViewHolder(
                    ChatListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onClick
                )

            ChatItemViewType.PRIVATE.ordinal ->
                PrivateChatViewHolder(
                    ChatListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onClick
                )

            else -> UnknownViewHolder(
                ChatListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    private fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) return "정보 없음"

        val currentTimeMillis = System.currentTimeMillis()

        val currentTime = Date(currentTimeMillis)
        val messageTime = Date(timestamp)

        val diff = currentTime.time - messageTime.time
        val minute = 60 * 1000
        val hour = minute * 60
        val day = hour * 24

        val calendar = Calendar.getInstance()
        calendar.time = messageTime
        val messageYear = calendar.get(Calendar.YEAR)
        calendar.time = Date(currentTimeMillis)
        val currentYear = calendar.get(Calendar.YEAR)

        return when {
            diff < minute -> "방금 전"
            diff < 2 * minute -> "1분 전"
            diff < hour -> "${diff / minute}분 전"
            diff < 2 * hour -> "1시간 전"
            diff < day -> "${diff / hour}시간 전"
            diff < 2 * day -> "어제"
            messageYear == currentYear -> SimpleDateFormat("MM월 dd일", Locale.KOREA).format(
                messageTime
            )

            else -> SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(messageTime)
        }
    }


    // 뷰 홀더
    inner class GroupChatViewHolder(
        private val binding: ChatListItemBinding,
        private val onClick: (ChatItem) -> Unit
    ) : ViewHolder(binding.root) {

        override fun onBind(item: ChatItem) = with(binding) {
            if (item is ChatItem.GroupChatItem) {
                txtName.text = item.title
                txtLastMassage.text = item.lastMessage
                txtMemberNum.text = item.memberLimit
                txtTimestamp.text = formatTimestamp(item.timeStamp ?: System.currentTimeMillis())
            }
            itemView.setOnClickListener {
                onClick(item)
            }
        }
    }

    inner class PrivateChatViewHolder(
        private val binding: ChatListItemBinding,
        private val onClick: (ChatItem) -> Unit
    ) : ViewHolder(binding.root) {

        override fun onBind(item: ChatItem) = with(binding) {
            if (item is ChatItem.PrivateChatItem) {
                txtName.text = item.title
                txtLastMassage.text = item.lastMessage
                txtMemberNum.text = item.memberLimit
                txtTimestamp.text = formatTimestamp(item.timeStamp ?: System.currentTimeMillis())
            }
            itemView.setOnClickListener {
                onClick(item)
            }
        }
    }

    class UnknownViewHolder(
        binding: ChatListItemBinding
    ) : ViewHolder(binding.root) {

        override fun onBind(item: ChatItem) = Unit
    }
}