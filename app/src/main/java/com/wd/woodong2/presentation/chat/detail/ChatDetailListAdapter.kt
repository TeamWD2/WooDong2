package com.wd.woodong2.presentation.chat.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wd.woodong2.databinding.ChatDetailItemBinding

class ChatDetailListAdapter : ListAdapter<MessageItem, ChatDetailListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<MessageItem>() {
        override fun areItemsTheSame(
            oldItem: MessageItem,
            newItem: MessageItem
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: MessageItem,
            newItem: MessageItem
        ): Boolean = oldItem == newItem
    }) {

    abstract class ViewHolder(
        root: View
    ) : RecyclerView.ViewHolder(root) {
        abstract fun onBind(item: MessageItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = OpponentMessageViewHolder(
        ChatDetailItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class OpponentMessageViewHolder(
        private val binding: ChatDetailItemBinding,
    ) : ViewHolder(binding.root) {
        override fun onBind(item: MessageItem) = with(binding) {
            txtName.text = item.senderId
            txtChat.text = item.message
        }
    }
}