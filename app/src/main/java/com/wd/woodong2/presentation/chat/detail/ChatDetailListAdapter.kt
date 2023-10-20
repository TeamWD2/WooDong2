package com.wd.woodong2.presentation.chat.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wd.woodong2.databinding.ChatDetailMyItemBinding
import com.wd.woodong2.databinding.ChatDetailOpponentItemBinding

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

    enum class MessageViewType {
        MY_MESSAGE, OPPONENT_MESSAGE
    }


    abstract class ViewHolder(
        root: View
    ) : RecyclerView.ViewHolder(root) {
        abstract fun onBind(item: MessageItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return when (MessageViewType.values()[viewType]) {
            MessageViewType.MY_MESSAGE -> {
                MyMessageViewHolder(
                    ChatDetailMyItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            MessageViewType.OPPONENT_MESSAGE -> {
                OpponentMessageViewHolder(
                    ChatDetailOpponentItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).isMyMessage) {
            true -> MessageViewType.MY_MESSAGE.ordinal
            false -> MessageViewType.OPPONENT_MESSAGE.ordinal
        }
    }

    class OpponentMessageViewHolder(
        private val binding: ChatDetailOpponentItemBinding,
    ) : ViewHolder(binding.root) {
        override fun onBind(item: MessageItem) = with(binding) {
            txtName.text = item.senderId
            txtChat.text = item.content
        }
    }

    class MyMessageViewHolder(
        private val binding: ChatDetailMyItemBinding,
    ) : ViewHolder(binding.root) {
        override fun onBind(item: MessageItem) = with(binding) {
            val params = txtChat.layoutParams as ConstraintLayout.LayoutParams
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            params.startToStart = ConstraintLayout.LayoutParams.UNSET
            txtChat.layoutParams = params
            txtChat.text = item.content
        }
    }
}