package com.wd.woodong2.presentation.chat.detail

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wd.woodong2.R
import com.wd.woodong2.databinding.ChatDetailMyItemBinding
import com.wd.woodong2.databinding.ChatDetailOpponentItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        abstract fun onBind(
            currentItem: MessageItem,
            previousItem: MessageItem?,
            nextItem: MessageItem?,
        )
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
        val nextItem = if (position < itemCount - 1) getItem(position + 1) else null
        holder.onBind(currentItem, previousItem, nextItem)
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
        /**
         * 1분이 지났는지 체크하는 메소드
         * */
        private fun isOverAMinute(oldTimestamp: Long, newTimestamp: Long): Boolean {
            val oldCalendar = Calendar.getInstance().apply { timeInMillis = oldTimestamp }
            val newCalendar = Calendar.getInstance().apply { timeInMillis = newTimestamp }

            val oldMinute = oldCalendar.get(Calendar.MINUTE)
            val newMinute = newCalendar.get(Calendar.MINUTE)

            return newMinute != oldMinute
        }


        override fun onBind(
            currentItem: MessageItem,
            previousItem: MessageItem?,
            nextItem: MessageItem?,
        ) = with(binding) {
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
            if (previousItem != null && previousItem.senderId == currentItem.senderId) {
                // 이어지는 메시지 중 마지막 메시지
                if (nextItem == null || currentItem.senderId != nextItem.senderId) {
                    val format = SimpleDateFormat("a h:mm", Locale.KOREA)
                    val formattedDate = format.format(Date(currentItem.timestamp ?: 0))
                    txtTimestamp.text = formattedDate
                }
                // 이어지는 메시지 중 중간 메시지
                else {
                    if (isOverAMinute(currentItem.timestamp ?: 0, nextItem.timestamp ?: 0)) {
                        val format = SimpleDateFormat("a h:mm", Locale.KOREA)
                        val formattedDate = format.format(Date(currentItem.timestamp ?: 0))
                        txtTimestamp.text = formattedDate
                    } else {
                        txtTimestamp.text = ""
                    }
                }

                cardView.visibility = View.GONE
                txtName.visibility = View.GONE

                params.setMargins(pxValue.toInt(), 0, 0, 0) // 왼쪽 마진을 10dp로 설정
            } else {
                // 초기 메시지
                cardView.visibility = View.VISIBLE
                imgProfile.visibility = View.VISIBLE
                txtName.visibility = View.VISIBLE

                imgProfile.load(currentItem.profileImg) {
                    error(R.drawable.public_default_wd2_ivory)
                }

                if (previousItem?.senderId != currentItem.senderId && currentItem.senderId != nextItem?.senderId) {
                    val format = SimpleDateFormat("a h:mm", Locale.KOREA)
                    val formattedDate = format.format(Date(currentItem.timestamp ?: 0))
                    txtTimestamp.text = formattedDate
                } else {
                    if (isOverAMinute(currentItem.timestamp ?: 0, nextItem?.timestamp ?: 0)) {
                        val format = SimpleDateFormat("a h:mm", Locale.KOREA)
                        val formattedDate = format.format(Date(currentItem.timestamp ?: 0))
                        txtTimestamp.text = formattedDate
                    } else {
                        txtTimestamp.text = ""
                    }
                }

                params.setMargins(0, 0, 0, 0) // 마진을 원래대로 설정
            }
            constraintChat.layoutParams = params
        }
    }


    class MyMessageViewHolder(
        private val binding: ChatDetailMyItemBinding,
    ) : ViewHolder(binding.root) {

        /**
         * 1분이 지났는지 체크하는 메소드
         * */
        private fun isOverAMinute(oldTimestamp: Long, newTimestamp: Long): Boolean {
            val oldCalendar = Calendar.getInstance().apply { timeInMillis = oldTimestamp }
            val newCalendar = Calendar.getInstance().apply { timeInMillis = newTimestamp }

            val oldMinute = oldCalendar.get(Calendar.MINUTE)
            val newMinute = newCalendar.get(Calendar.MINUTE)

            return newMinute != oldMinute
        }

        override fun onBind(
            currentItem: MessageItem,
            previousItem: MessageItem?,
            nextItem: MessageItem?,
        ) = with(binding) {
            val params = txtChat.layoutParams as ConstraintLayout.LayoutParams
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            params.startToStart = ConstraintLayout.LayoutParams.UNSET
            txtChat.layoutParams = params
            txtChat.text = currentItem.content

            // 이어지는 메시지
            if (previousItem != null && previousItem.senderId == currentItem.senderId) {
                // 이어지는 메시지 중 마지막 메시지
                if (nextItem == null || currentItem.senderId != nextItem.senderId) {
                    val format = SimpleDateFormat("a h:mm", Locale.KOREA)
                    val formattedDate = format.format(Date(currentItem.timestamp ?: 0))
                    txtTimestamp.text = formattedDate
                }
                // 이어지는 메시지 중 중간 메시지
                else {
                    if (isOverAMinute(currentItem.timestamp ?: 0, nextItem.timestamp ?: 0)) {
                        val format = SimpleDateFormat("a h:mm", Locale.KOREA)
                        val formattedDate = format.format(Date(currentItem.timestamp ?: 0))
                        txtTimestamp.text = formattedDate
                    } else {
                        txtTimestamp.text = ""
                    }
                }
            } else {
                // 초기 메시지
                if (previousItem?.senderId != currentItem.senderId && currentItem.senderId != nextItem?.senderId) {
                    val format = SimpleDateFormat("a h:mm", Locale.KOREA)
                    val formattedDate = format.format(Date(currentItem.timestamp ?: 0))
                    txtTimestamp.text = formattedDate
                } else {
                    if (isOverAMinute(currentItem.timestamp ?: 0, nextItem?.timestamp ?: 0)) {
                        val format = SimpleDateFormat("a h:mm", Locale.KOREA)
                        val formattedDate = format.format(Date(currentItem.timestamp ?: 0))
                        txtTimestamp.text = formattedDate
                    } else {
                        txtTimestamp.text = ""
                    }
                }

                params.setMargins(0, 0, 0, 0) // 마진을 원래대로 설정
            }

        }
    }
}