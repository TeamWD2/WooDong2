package com.wd.woodong2.presentation.chat.detail

import android.util.TypedValue
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
            newItem: MessageItem,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: MessageItem,
            newItem: MessageItem,
        ): Boolean = oldItem == newItem
    }) {

    enum class MessageViewType {
        MY_MESSAGE, OPPONENT_MESSAGE
    }


    abstract class ViewHolder(
        root: View,
    ) : RecyclerView.ViewHolder(root) {
        abstract fun onBind(currentItem: MessageItem, previousItem: MessageItem?)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
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
        val currentItem = getItem(position)
        val previousItem = if (position > 0) getItem(position - 1) else null
        holder.onBind(currentItem, previousItem)
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
        override fun onBind(currentItem: MessageItem, previousItem: MessageItem?) = with(binding) {
            txtName.text = currentItem.nickname
            txtChat.text = currentItem.content

            // TODO 변경 예정
            val dpValue = 60

            val pxValue = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue.toFloat(),
                binding.root.context.resources.displayMetrics
            )
            val params = constraintChat.layoutParams as ViewGroup.MarginLayoutParams

            // 이어지는 메시지
            if (previousItem == null || previousItem.senderId == currentItem.senderId) {
                cardView.visibility = View.GONE
                txtName.visibility = View.GONE

                params.setMargins(pxValue.toInt(), 0, 0, 0) // 왼쪽 마진을 10dp로 설정
            } else {
                // 초기 메시지
                cardView.visibility = View.VISIBLE
                txtName.visibility = View.VISIBLE

                params.setMargins(0, 0, 0, 0) // 마진을 원래대로 설정
            }
            constraintChat.layoutParams = params
        }
    }


    class MyMessageViewHolder(
        private val binding: ChatDetailMyItemBinding,
    ) : ViewHolder(binding.root) {
        override fun onBind(currentItem: MessageItem, previousItem: MessageItem?) = with(binding) {
            val params = txtChat.layoutParams as ConstraintLayout.LayoutParams
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            params.startToStart = ConstraintLayout.LayoutParams.UNSET
            txtChat.layoutParams = params

            txtChat.text = currentItem.content
        }
    }
}