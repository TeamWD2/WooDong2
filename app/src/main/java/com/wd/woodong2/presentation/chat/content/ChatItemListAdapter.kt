package com.wd.woodong2.presentation.chat.content

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wd.woodong2.databinding.ChatListItemBinding

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


    // 뷰 홀더
    class GroupChatViewHolder(
        private val binding: ChatListItemBinding,
        private val onClick: (ChatItem) -> Unit
    ) : ViewHolder(binding.root) {

        override fun onBind(item: ChatItem) = with(binding) {
            if (item is ChatItem.GroupChatItem) {
                txtName.text = item.title
                txtLastMassage.text = item.lastMessage
                txtLocale.text = item.location
                txtDate.text = item.timeStamp
            }
            itemView.setOnClickListener {
                onClick(item)
            }
        }
    }

    class PrivateChatViewHolder(
        private val binding: ChatListItemBinding,
        private val onClick: (ChatItem) -> Unit
    ) : ViewHolder(binding.root) {

        override fun onBind(item: ChatItem) = with(binding) {
            if (item is ChatItem.PrivateChatItem) {
                txtName.text = item.userName
                txtLastMassage.text = item.lastMessage
                txtLocale.text = item.location
                txtDate.text = item.timeStamp
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